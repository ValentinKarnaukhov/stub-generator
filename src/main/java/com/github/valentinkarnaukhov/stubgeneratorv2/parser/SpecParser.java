package com.github.valentinkarnaukhov.stubgeneratorv2.parser;

import com.github.valentinkarnaukhov.stubgenerator.model.GeneratorProperties;
import io.swagger.codegen.v3.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Valentin Karnaukhov
 */
public class SpecParser {

    private final CodegenConfig config;
    private final OpenAPI openAPI;
    private final GeneratorProperties properties;

    public SpecParser(ClientOptInput optInput, GeneratorProperties properties) {
        this.config = optInput.getConfig();
        this.openAPI = optInput.getOpenAPI();
        this.properties = properties;
    }

    public CodegenOperation getCodegenOperation(String path, String methodName, Operation operation) {
        return config.fromOperation(path, methodName, operation, openAPI.getComponents().getSchemas(), openAPI);
    }

    public String getGroupIdentifier(CodegenOperation cgOperation, Operation operation) {
        if (properties.getUseTags() && !operation.getTags().isEmpty()) {
            return StringUtils.capitalize(operation.getTags().iterator().next());
        } else {
            return StringUtils.capitalize(cgOperation.getPath().split("/")[1]);
        }
    }

    public CodegenConfig getConfig() {
        return config;
    }

    public OpenAPI getOpenAPI() {
        return openAPI;
    }

    public Map<String, CodegenModel> extractModels() {
        final Map<String, CodegenModel> allModels = new HashMap<>();

        final Map<String, Schema> schemas = this.openAPI.getComponents().getSchemas();
        if (schemas == null) {
            return allModels;
        }

        Set<String> modelKeys = schemas.keySet();
        Map<String, Object> allProcessedModels = new TreeMap<>((o1, o2) -> ObjectUtils.compare(config.toModelName(o1), config.toModelName(o2)));

        for (String name : modelKeys) {
            try {
                Schema schema = schemas.get(name);
                Map<String, Schema> schemaMap = new HashMap<>();
                schemaMap.put(name, schema);
                Map<String, Object> models = processModels(config, schemaMap, schemas);
                models.put("classname", config.toModelName(name));
                models.putAll(config.additionalProperties());
                allProcessedModels.put(name, models);
            } catch (Exception e) {
                throw new RuntimeException("Could not process model '" + name + "'" + ".Please make sure that your schema is correct!", e);
            }
        }

        final ISchemaHandler schemaHandler = config.getSchemaHandler();
        schemaHandler.readProcessedModels(allProcessedModels);

        final List<CodegenModel> composedModels = schemaHandler.getModels();

        if (composedModels != null && !composedModels.isEmpty()) {
            for (CodegenModel composedModel : composedModels) {
                final Map<String, Object> models = processModel(composedModel, config, schemas);
                models.put("classname", config.toModelName(composedModel.name));
                models.putAll(config.additionalProperties());
                allProcessedModels.put(composedModel.name, models);
            }
        }

        allProcessedModels = config.postProcessAllModels(allProcessedModels);

        for (String modelName : allProcessedModels.keySet()) {
            Map<String, Object> models = (Map<String, Object>) allProcessedModels.get(modelName);
            try {
                Map<String, Object> modelTemplate = (Map<String, Object>) ((List<Object>) models.get("models")).get(0);
                allModels.put(config.toModelName(modelName), (CodegenModel) modelTemplate.get("model"));
            } catch (Exception e) {
                throw new RuntimeException("Could not generate model '" + modelName + "'", e);
            }
        }

        return allModels;
    }

    private Map<String, Object> processModels(CodegenConfig config, Map<String, Schema> definitions, Map<String, Schema> allDefinitions) {
        Map<String, Object> objs = new HashMap<>();
        objs.put("package", config.modelPackage());
        List<Object> models = new ArrayList<>();
        Set<String> allImports = new LinkedHashSet<>();
        for (String key : definitions.keySet()) {
            Schema schema = definitions.get(key);
            CodegenModel cm = config.fromModel(key, schema, allDefinitions);
            Map<String, Object> mo = new HashMap<>();
            mo.put("model", cm);
            mo.put("schema", schema);
            mo.put("importPath", config.toModelImport(cm.classname));
            models.add(mo);

            allImports.addAll(cm.imports);
        }
        objs.put("models", models);
        Set<String> importSet = new TreeSet<>();
        for (String nextImport : allImports) {
            String mapping = config.importMapping().get(nextImport);
            if (mapping == null) {
                mapping = config.toModelImport(nextImport);
            }
            if (mapping != null && !config.defaultIncludes().contains(mapping)) {
                importSet.add(mapping);
            }
            // add instantiation types
            mapping = config.instantiationTypes().get(nextImport);
            if (mapping != null && !config.defaultIncludes().contains(mapping)) {
                importSet.add(mapping);
            }
        }
        List<Map<String, String>> imports = new ArrayList<>();
        for (String s : importSet) {
            Map<String, String> item = new HashMap<>();
            item.put("import", s);
            imports.add(item);
        }
        objs.put("imports", imports);
        config.postProcessModels(objs);
        return objs;
    }

    private Map<String, Object> processModel(CodegenModel codegenModel, CodegenConfig config, Map<String, Schema> allDefinitions) {
        Map<String, Object> objs = new HashMap<>();
        objs.put("package", config.modelPackage());
        List<Object> models = new ArrayList<>();

        objs.put("x-is-composed-model", codegenModel.isComposedModel);

        Map<String, Object> modelObject = new HashMap<>();
        modelObject.put("model", codegenModel);
        modelObject.put("importPath", config.toModelImport(codegenModel.classname));

        Set<String> allImports = new LinkedHashSet<>();
        allImports.addAll(codegenModel.imports);
        models.add(modelObject);

        objs.put("models", models);
        Set<String> importSet = new TreeSet<>();
        for (String nextImport : allImports) {
            String mapping = config.importMapping().get(nextImport);
            if (mapping == null) {
                mapping = config.toModelImport(nextImport);
            }
            if (mapping != null && !config.defaultIncludes().contains(mapping)) {
                importSet.add(mapping);
            }
            // add instantiation types
            mapping = config.instantiationTypes().get(nextImport);
            if (mapping != null && !config.defaultIncludes().contains(mapping)) {
                importSet.add(mapping);
            }
        }
        List<Map<String, String>> imports = new ArrayList<>();
        for (String s : importSet) {
            Map<String, String> item = new HashMap<>();
            item.put("import", s);
            imports.add(item);
        }
        objs.put("imports", imports);
        config.postProcessModels(objs);
        return objs;
    }
}
