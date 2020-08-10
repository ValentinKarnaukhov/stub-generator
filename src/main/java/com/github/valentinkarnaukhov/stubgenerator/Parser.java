package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.model.*;
import com.github.valentinkarnaukhov.stubgenerator.util.ModelResolver;
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

        for (CodegenResponse response : oper.getResponses()) {
            ResponseTemplate responseTemplate = processResponse(response, responses);
            pathTemplate.getResponses().add(responseTemplate);
        }

        return pathTemplate;
    }

    private List<FieldTemplate> processQueryParameters(List<CodegenParameter> parameters) {
        return modelResolver.resolveParameter(parameters, generator.getMaxDepth());
    }


    private ResponseTemplate processResponse(CodegenResponse response, ApiResponses responses) {
        ResponseTemplate responseTemplate = new ResponseTemplate();
        responseTemplate.setCode(response.getCode());
        responseTemplate.setResponseType(response.getDataType());
        responseTemplate.setResponseFields(new ArrayList<>());

        CodegenModel responseModel = generator.getAllModels().get(response.getBaseType());
        if (responseModel != null) {
            List<FieldTemplate> responseFields = modelResolver.resolveFlatten(responseModel, generator.getMaxDepth());
            responseTemplate.setResponseFields(responseFields);
        } else {
            responseTemplate.setDescription(responses.get(response.getCode()).getDescription());
        }

        return responseTemplate;
    }

}
