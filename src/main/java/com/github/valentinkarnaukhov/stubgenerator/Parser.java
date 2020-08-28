package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.model.*;
import com.github.valentinkarnaukhov.stubgenerator.resolver.ModelResolver;
import com.github.valentinkarnaukhov.stubgenerator.resolver.ResolverConfFactory;
import io.swagger.codegen.v3.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    private final WiremockGenerator generator;
    private ModelResolver queryParamResolver;
    private ModelResolver bodyParamResolver;
    private ModelResolver responseResolver;

    public Parser(WiremockGenerator generator) {
        this.generator = generator;
    }

    public List<TagTemplate> parse() {
        this.queryParamResolver = new ModelResolver(this.generator.getAllModels(), this.generator.getMaxDepth(), ResolverConfFactory.getForQuery(this.generator));
        this.bodyParamResolver = new ModelResolver(this.generator.getAllModels(), this.generator.getMaxDepth(), ResolverConfFactory.getForBody(this.generator));
        this.responseResolver = new ModelResolver(this.generator.getAllModels(), this.generator.getMaxDepth(), ResolverConfFactory.getForResponse(this.generator));

        OpenAPI openAPI = generator.getOpenAPI();

        Paths paths = openAPI.getPaths();

        Map<String, List<PathTemplate>> pathTemplates = new LinkedHashMap<>();
        for (String pathItem : openAPI.getPaths().keySet()) {

            Map<PathItem.HttpMethod, Operation> operationMap = paths.get(pathItem).readOperationsMap();
            for (PathItem.HttpMethod method : operationMap.keySet()) {
                Operation operation = operationMap.get(method);

                CodegenOperation codegenOperation =
                        generator.getConfig().fromOperation(pathItem, method.name(), operation, openAPI.getComponents().getSchemas(), openAPI);
                PathTemplate pathTemplate = operationToPath(codegenOperation);

                pathTemplates.computeIfPresent(getGroupIdentifier(codegenOperation, operation), (k, v) -> {
                    v.add(pathTemplate);
                    return v;
                });

                pathTemplates.computeIfAbsent(getGroupIdentifier(codegenOperation, operation), k -> {
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

    private String getGroupIdentifier(CodegenOperation cgOperation, Operation operation) {
        if (generator.getUseTags() && !operation.getTags().isEmpty()) {
            return StringUtils.capitalize(operation.getTags().iterator().next());
        } else {
            return StringUtils.capitalize(cgOperation.getPath().split("/")[1]);
        }
    }

    private PathTemplate operationToPath(CodegenOperation oper) {
        PathTemplate pathTemplate = new PathTemplate();
        pathTemplate.setOperationId(StringUtils.capitalize(oper.getOperationId()));
        pathTemplate.setPath(oper.getPath());
        pathTemplate.setHttpMethod(oper.getHttpMethod().toLowerCase());
        pathTemplate.setResponses(new ArrayList<>());

        List<ObjectTemplate> queryParams = processQueryParameters(oper.getQueryParams());
        pathTemplate.setQueryParams(queryParams);

        List<ObjectTemplate> bodyParams = processBodyParameters(oper.getBodyParams());
        pathTemplate.setBodyParams(bodyParams);

        List<ResponseTemplate> responsesParams = new ArrayList<>();
        for (CodegenResponse response : oper.getResponses()) {
            responsesParams.add(processResponse(response));
        }
        pathTemplate.setResponses(responsesParams);

        pathTemplate.getCollections().addAll(queryParamResolver.getCollections());
        pathTemplate.getCollections().addAll(bodyParamResolver.getCollections());
        pathTemplate.getCollections().addAll(responseResolver.getCollections());
        Supplier<Stream<ObjectTemplate>> paramsSup = () -> {
            Stream<ObjectTemplate> params = Stream.concat(queryParams.stream(), bodyParams.stream());
            params = Stream.concat(params, responsesParams.stream().filter(v -> v.getResponse() != null)
                    .map(ResponseTemplate::getResponse));
            return params;
        };
        pathTemplate.getCollections().addAll(paramsSup.get()
                .filter(ObjectTemplate::isCollection)
                .collect(Collectors.toList()));
        return pathTemplate;
    }

    private List<ObjectTemplate> processQueryParameters(List<CodegenParameter> parameters) {
        return queryParamResolver.parametersToObjects(parameters);
    }

    private List<ObjectTemplate> processBodyParameters(List<CodegenParameter> parameters) {
        return bodyParamResolver.parametersToObjects(parameters);
    }


    private ResponseTemplate processResponse(CodegenResponse response) {
        ResponseTemplate responseTemplate = new ResponseTemplate();
        responseTemplate.setDescription(response.getMessage());
        responseTemplate.setCode(response.getCode());
        responseTemplate.setResponse(responseResolver.responseToObject(response));
        return responseTemplate;
    }

}
