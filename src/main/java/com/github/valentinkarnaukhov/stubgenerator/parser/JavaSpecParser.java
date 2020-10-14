package com.github.valentinkarnaukhov.stubgenerator.parser;

import com.github.valentinkarnaukhov.stubgenerator.model.GeneratorProperties;
import com.github.valentinkarnaukhov.stubgenerator.model.PathTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import io.swagger.codegen.v3.CodegenConfig;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
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

    //TODO implement it!!!
    private PathTemplate convertCodegenOperationToPathTemplate(CodegenOperation codegenOperation) {
        return null;
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
