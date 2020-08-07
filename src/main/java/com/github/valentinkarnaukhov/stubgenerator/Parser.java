package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.model.QueryParam;
import com.github.valentinkarnaukhov.stubgenerator.model.ResponseTemplate;
import io.swagger.codegen.v3.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import com.github.valentinkarnaukhov.stubgenerator.model.PathTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Parser {

    private final WiremockGenerator generator;

    public Parser(WiremockGenerator generator) {
        this.generator = generator;
    }

    public List<TagTemplate> parse() {

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
        pathTemplates.forEach((k, v) -> tagTemplates.add(new TagTemplate(k, v)));

        return tagTemplates;
    }

    private PathTemplate operationToPath(CodegenOperation oper, ApiResponses responses) {
        PathTemplate pathTemplate = new PathTemplate();
        pathTemplate.setOperationId(StringUtils.capitalize(oper.getOperationId()));
        pathTemplate.setPath(oper.getPath());
        pathTemplate.setHttpMethod(oper.getHttpMethod().toLowerCase());
        pathTemplate.setResponses(new ArrayList<>());

        List<QueryParam> queryParams = new ArrayList<>();
        for (CodegenParameter parameter : oper.getQueryParams()) {
            QueryParam queryParam = QueryParam.builder()
                    .methodQueryParamName(parameter.getParamName())
                    .queryParamName(StringUtils.capitalize(parameter.getParamName()))
                    .queryParamType(parameter.getDataType())
                    .build();
            queryParams.add(queryParam);
        }
        pathTemplate.setQueryParams(queryParams);

        for (CodegenResponse response : oper.getResponses()) {
            ResponseTemplate responseTemplate = new ResponseTemplate();
            responseTemplate.setCode(response.getCode());
            responseTemplate.setResponseType(response.getDataType());
            responseTemplate.setResponseSetters(new ArrayList<>());

            CodegenModel responseModel = generator.getAllModels().get(response.getBaseType());
            if (responseModel != null) {
                for (CodegenProperty property : responseModel.getAllVars()) {
                    ResponseTemplate.ResponseField responseField =
                            ResponseTemplate.ResponseField.builder()
                                    .setterName(property.getSetter())
                                    .responseFieldName(StringUtils.capitalize(property.getName()))
                                    .responseFieldType(property.getDatatype())
                                    .methodResponseFieldName(property.getName())
                                    .build();
                    responseTemplate.getResponseSetters().add(responseField);
                }
            } else {
                responseTemplate.setDescription(responses.get(response.getCode()).getDescription());
            }

            pathTemplate.getResponses().add(responseTemplate);
        }

        return pathTemplate;
    }

}
