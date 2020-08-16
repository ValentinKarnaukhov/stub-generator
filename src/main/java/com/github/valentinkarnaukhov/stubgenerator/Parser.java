package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.model.*;
import com.github.valentinkarnaukhov.stubgenerator.resolver.ModelResolver;
import com.github.valentinkarnaukhov.stubgenerator.resolver.ResolverConfFactory;
import io.swagger.codegen.v3.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    private final WiremockGenerator generator;
    private ModelResolver modelResolver;
    private ModelResolver queryParamResolver;
    private ModelResolver bodyParamResolver;

    public Parser(WiremockGenerator generator) {
        this.generator = generator;
    }

    public List<TagTemplate> parse() {
        this.queryParamResolver = new ModelResolver(this.generator.getAllModels(), ResolverConfFactory.getForQuery());
        this.bodyParamResolver = new ModelResolver(this.generator.getAllModels(), ResolverConfFactory.getForBody());
        this.modelResolver = new ModelResolver(this.generator.getAllModels(), ResolverConfFactory.getForQuery());

        OpenAPI openAPI = generator.getOpenAPI();

        Paths paths = openAPI.getPaths();

        Map<String, List<PathTemplate>> pathTemplates = new LinkedHashMap<>();
        for (String pathItem : openAPI.getPaths().keySet()) {

            Map<PathItem.HttpMethod, Operation> operationMap = paths.get(pathItem).readOperationsMap();
            for (PathItem.HttpMethod method : operationMap.keySet()) {
                Operation operation = operationMap.get(method);

                CodegenOperation codegenOperation =
                        generator.getConfig().fromOperation(pathItem, method.name(), operation, openAPI.getComponents().getSchemas(), openAPI);
                PathTemplate pathTemplate = operationToPath(codegenOperation, operation.getResponses());

                pathTemplates.computeIfPresent(operation.getTags().iterator().next(), (k, v) -> {
                    v.add(pathTemplate);
                    return v;
                });

                pathTemplates.computeIfAbsent(operation.getTags().iterator().next(), k -> {
                    List<PathTemplate> toAdd = new ArrayList<>();
                    toAdd.add(pathTemplate);
                    return toAdd;
                });
            }
        }

        List<TagTemplate> tagTemplates = new ArrayList<>();
        pathTemplates.forEach((k, v) -> tagTemplates.add(new TagTemplate(k + "Mock", v)));

        return tagTemplates;
    }

    private PathTemplate operationToPath(CodegenOperation oper, ApiResponses responses) {
        PathTemplate pathTemplate = new PathTemplate();
        pathTemplate.setOperationId(StringUtils.capitalize(oper.getOperationId()));
        pathTemplate.setPath(oper.getPath());
        pathTemplate.setHttpMethod(oper.getHttpMethod().toLowerCase());
        pathTemplate.setResponses(new ArrayList<>());

        List<ObjectTemplate> queryParams = processQueryParameters(oper.getQueryParams());
        pathTemplate.setQueryParams(queryParams);

        List<ObjectTemplate> bodyParams = processBodyParameters(oper.getBodyParams());
        pathTemplate.setBodyParams(bodyParams);

        for (CodegenResponse response : oper.getResponses()) {
            ObjectTemplateTmp responseTemplate = processResponse(response, responses);
            pathTemplate.getResponses().add(responseTemplate);
        }
        pathTemplate.getCollections().addAll(queryParamResolver.getCollections());
        pathTemplate.getCollections().addAll(bodyParamResolver.getCollections());
        Stream<ObjectTemplate> params = Stream.concat(queryParams.stream(), bodyParams.stream());
        pathTemplate.getCollections().addAll(params
                .map(ObjectTemplate::toField)
                .filter(Field::isCollection)
                .collect(Collectors.toList()));
        return pathTemplate;
    }

    private List<ObjectTemplate> processQueryParameters(List<CodegenParameter> parameters) {
        return queryParamResolver.parametersToObject(parameters);
    }

    private List<ObjectTemplate> processBodyParameters(List<CodegenParameter> parameters) {
        return bodyParamResolver.parametersToObject(parameters);
    }


    private ObjectTemplateTmp processResponse(CodegenResponse response, ApiResponses responses) {
        ObjectTemplateTmp responseTemplate = new ObjectTemplateTmp();
        responseTemplate.setCode(response.getCode());
        responseTemplate.setObjectType(response.getDataType());
        responseTemplate.setFields(new ArrayList<>());

        CodegenModel responseModel = generator.getAllModels().get(response.getBaseType());
        if (responseModel != null) {
            List<FieldTemplate> responseFields = modelResolver.resolveFlatten(responseModel, ResolverConfFactory.getForResponse());
            responseTemplate.setFields(responseFields);
        } else {
            responseTemplate.setDescription(responses.get(response.getCode()).getDescription());
        }

        return responseTemplate;
    }

}
