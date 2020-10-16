package com.github.valentinkarnaukhov.stubgenerator.parser;

import com.github.valentinkarnaukhov.stubgenerator.model.*;
import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;
import com.github.valentinkarnaukhov.stubgenerator.resolver.v2.ModelResolver;
import io.swagger.codegen.v3.CodegenConfig;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Valentin Karnaukhov
 */
public class JavaSpecParser extends AbstractSpecParser implements SpecParser {

    private final CodegenConfig config;
    private final GeneratorProperties properties;

    @Setter
    private ModelResolver queryParamResolver;
    @Setter
    private ModelResolver bodyParamResolver;
    @Setter
    private ModelResolver responseResolver;

    private final Map<String, List<PathTemplate>> pathTemplates = new LinkedHashMap<>();

    public JavaSpecParser(CodegenConfig config, GeneratorProperties properties) {
        this.config = config;
        this.properties = properties;
    }

    @Override
    public List<TagTemplate> parse(OpenAPI spec) {
        Paths paths = spec.getPaths();

        for (String httpPath : paths.keySet()) {
            Map<PathItem.HttpMethod, Operation> operations = paths.get(httpPath).readOperationsMap();

            for (PathItem.HttpMethod httpMethod : operations.keySet()) {
                Operation specOperation = operations.get(httpMethod);
                CodegenOperation codegenOperation = config.fromOperation(httpPath, httpMethod.name(), specOperation, spec.getComponents().getSchemas(), spec);

                PathTemplate pathTemplate = convertCodegenOperationToPathTemplate(codegenOperation);
                String identifier = getGroupIdentifier(codegenOperation, specOperation);
                putToResult(identifier, pathTemplate);
            }
        }

        return compileTagTemplates();
    }

    private PathTemplate convertCodegenOperationToPathTemplate(CodegenOperation codegenOperation) {
        PathTemplate pathTemplate = new PathTemplate();
        pathTemplate.setOperationId(StringUtils.capitalize(codegenOperation.getOperationId()));
        pathTemplate.setPath(codegenOperation.getPath());
        pathTemplate.setHttpMethod(codegenOperation.getHttpMethod().toLowerCase());
        pathTemplate.setResponses(new ArrayList<>());

        List<ObjectTemplate> queryParams = extractQueryParams(codegenOperation);
        pathTemplate.setQueryParams(queryParams);

        List<ObjectTemplate> bodyParams = extractBodyParams(codegenOperation);
        pathTemplate.setBodyParams(bodyParams);

        List<ResponseTemplate> responseTemplates = extractResponses(codegenOperation);
        pathTemplate.setResponses(responseTemplates);

        return pathTemplate;
    }

    private List<ObjectTemplate> extractQueryParams(CodegenOperation codegenOperation) {
        return codegenOperation
                .getQueryParams()
                .stream()
                .map(CodegenParameter::fromParameter)
                .map(queryParamResolver::toObjectTemplate)
                .collect(Collectors.toList());
    }

    private List<ObjectTemplate> extractBodyParams(CodegenOperation codegenOperation) {
        return codegenOperation
                .getBodyParams()
                .stream()
                .map(CodegenParameter::fromParameter)
                .map(bodyParamResolver::toObjectTemplate)
                .collect(Collectors.toList());
    }

    private List<ResponseTemplate> extractResponses(CodegenOperation codegenOperation) {
        List<ResponseTemplate> responsesParams = new ArrayList<>();

        for (CodegenResponse response : codegenOperation.getResponses()) {
            CodegenParameter parameter = CodegenParameter.fromResponse(response);
            ObjectTemplate responseBody = responseResolver.toObjectTemplate(parameter);

            ResponseTemplate responseTemplate = new ResponseTemplate(responseBody);
            responseTemplate.setCode(response.getCode());
            responseTemplate.setDescription(response.getMessage());

            responsesParams.add(responseTemplate);
        }
        return responsesParams;
    }


    private void putToResult(String identifier, PathTemplate pathTemplate) {
        if (!pathTemplates.containsKey(identifier)) {
            pathTemplates.put(identifier, new ArrayList<>());
        }
        pathTemplates.get(identifier).add(pathTemplate);
    }

    private List<TagTemplate> compileTagTemplates() {
        return pathTemplates.entrySet().stream().map(e -> new TagTemplate(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    private String getGroupIdentifier(CodegenOperation cgOperation, Operation operation) {
        if (properties.getUseTags() && !operation.getTags().isEmpty()) {
            return StringUtils.capitalize(operation.getTags().iterator().next());
        } else {
            return StringUtils.capitalize(cgOperation.getPath().split("/")[1]);
        }
    }
}
