package com.github.valentinkarnaukhov.stubgeneratorv2.processor;

import com.github.valentinkarnaukhov.stubgenerator.model.ObjectTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.PathTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.ResponseTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import com.github.valentinkarnaukhov.stubgeneratorv2.parser.SpecParser;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenParameter;
import io.swagger.codegen.v3.CodegenResponse;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Valentin Karnaukhov
 */
public class SpecProcessor {

    private final SpecParser specParser;

    public SpecProcessor(SpecParser specParser) {
        this.specParser = specParser;
    }

    public List<TagTemplate> process() {
        Paths paths = specParser.getOpenAPI().getPaths();

        Map<String, List<PathTemplate>> pathTemplates = new LinkedHashMap<>();
        for (String pathItem : paths.keySet()) {

            Map<PathItem.HttpMethod, Operation> operationMap = paths.get(pathItem).readOperationsMap();
            for (PathItem.HttpMethod method : operationMap.keySet()) {
                Operation operation = operationMap.get(method);

                CodegenOperation codegenOperation = specParser.getCodegenOperation(pathItem, method.name(), operation);
                PathTemplate pathTemplate = operationToPath(codegenOperation);

                pathTemplates.computeIfPresent(specParser.getGroupIdentifier(codegenOperation, operation), (k, v) -> {
                    v.add(pathTemplate);
                    return v;
                });

                pathTemplates.computeIfAbsent(specParser.getGroupIdentifier(codegenOperation, operation), k -> {
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
        return null;
    }

    private List<ObjectTemplate> processBodyParameters(List<CodegenParameter> parameters) {
        return null;
    }


    private ResponseTemplate processResponse(CodegenResponse response) {
        ResponseTemplate responseTemplate = new ResponseTemplate();
        responseTemplate.setDescription(response.getMessage());
        responseTemplate.setCode(response.getCode());
        responseTemplate.setResponse(null);
        return responseTemplate;
    }

}
