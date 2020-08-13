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

public class Parser {

    private final WiremockGenerator generator;
    private ModelResolver modelResolver;

    public Parser(WiremockGenerator generator) {
        this.generator = generator;
    }

    public List<TagTemplate> parse() {
        this.modelResolver = new ModelResolver(this.generator.getAllModels());

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

        List<FieldTemplate> queryParams = processQueryParameters(oper.getQueryParams());
        pathTemplate.setQueryParams(queryParams);

        List<FieldTemplate> bodyParams = processBodyParameters(oper.getBodyParams());
        pathTemplate.setBodyParams(bodyParams);

        for (CodegenResponse response : oper.getResponses()) {
            ObjectTemplate responseTemplate = processResponse(response, responses);
            pathTemplate.getResponses().add(responseTemplate);
        }
        return pathTemplate;
    }

    private List<FieldTemplate> processQueryParameters(List<CodegenParameter> parameters) {
        return modelResolver.resolveParameter(parameters, generator.getMaxDepth(), ResolverConfFactory.getForQuery());
    }

    private List<FieldTemplate> processBodyParameters(List<CodegenParameter> parameter) {
        return modelResolver.resolveParameter(parameter, generator.getMaxDepth(), ResolverConfFactory.getForBody());
    }


    private ObjectTemplate processResponse(CodegenResponse response, ApiResponses responses) {
        ObjectTemplate responseTemplate = new ObjectTemplate();
        responseTemplate.setCode(response.getCode());
        responseTemplate.setObjectType(response.getDataType());
        responseTemplate.setFields(new ArrayList<>());

        CodegenModel responseModel = generator.getAllModels().get(response.getBaseType());
        if (responseModel != null) {
            List<FieldTemplate> responseFields = modelResolver.resolveFlatten(responseModel, generator.getMaxDepth(), ResolverConfFactory.getForResponse());
            responseTemplate.setFields(responseFields);
        } else {
            responseTemplate.setDescription(responses.get(response.getCode()).getDescription());
        }

        return responseTemplate;
    }

}
