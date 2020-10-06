package com.github.valentinkarnaukhov.stubgenerator.parser;

import com.github.valentinkarnaukhov.stubgenerator.model.GeneratorProperties;
import com.github.valentinkarnaukhov.stubgenerator.model.PathTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;
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

/**
 * @author Valentin Karnaukhov
 */
public class JavaSpecParser extends AbstractSpecParser implements SpecParser {

    private final CodegenConfig config;
    private final GeneratorProperties properties;

    public JavaSpecParser(CodegenConfig config, GeneratorProperties properties) {
        this.config = config;
        this.properties = properties;
    }

    @Override
    public List<TagTemplate> parse(OpenAPI spec) {
        Paths paths = spec.getPaths();

        Map<String, List<PathTemplate>> pathTemplates = new LinkedHashMap<>();
        for (String pathItem : paths.keySet()) {

            Map<PathItem.HttpMethod, Operation> operationMap = paths.get(pathItem).readOperationsMap();
            for (PathItem.HttpMethod method : operationMap.keySet()) {
                Operation operation = operationMap.get(method);

                CodegenOperation codegenOperation =
                        config.fromOperation(pathItem, method.name(), operation, spec.getComponents().getSchemas(), spec);
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
        if (properties.getUseTags() && !operation.getTags().isEmpty()) {
            return StringUtils.capitalize(operation.getTags().iterator().next());
        } else {
            return StringUtils.capitalize(cgOperation.getPath().split("/")[1]);
        }
    }
}
