package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import com.github.valentinkarnaukhov.stubgenerator.mustache.MustacheProcessor;
import io.swagger.codegen.v3.*;
import io.swagger.codegen.v3.generators.DefaultCodegenConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class WiremockGenerator extends AbstractGenerator implements Generator {

    private DefaultCodegenConfig config;
    private OpenAPI openAPI;
    private Parser parser;
    private Map<String, CodegenModel> allModels;

    public WiremockGenerator(OpenAPI openAPI,
                             DefaultCodegenConfig defaultCodegenConfig) {
        config = defaultCodegenConfig;
        parser = new Parser(this);
        this.openAPI = openAPI;
        this.allModels = new HashMap<>();
        generateModels(this.allModels);
    }

    @Override
    public List<File> generate() {
        List<TagTemplate> templates = parser.parse();
        MustacheProcessor mustacheProcessor = new MustacheProcessor(config);
        try {
            for(TagTemplate template : templates){
                mustacheProcessor.process(template);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Generator opts(ClientOptInput opts) {
        return null;
    }

    private void generateModels(Map<String, CodegenModel> allModels) {
        final Map<String, Schema> schemas = this.openAPI.getComponents().getSchemas();
        if (schemas == null) {
            return;
        }

        Set<String> modelKeys = schemas.keySet();

        // store all processed models
        Map<String,Object> allProcessedModels = new TreeMap<>((o1, o2) -> ObjectUtils.compare(config.toModelName(o1), config.toModelName(o2)));

        // process models only
        for (String name : modelKeys) {
            try {
                Schema schema = schemas.get(name);
                Map<String, Schema> schemaMap = new HashMap<>();
                schemaMap.put(name, schema);
                Map<String, Object> models = processModels(config, schemaMap, schemas);
                models.put("classname", config.toModelName(name));
                models.putAll(config.additionalProperties());
                allProcessedModels.put(name, models);

                final List<Object> modelList  = (List<Object>) models.get("models");

                if (modelList == null || modelList.isEmpty()) {
                    continue;
                }
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

        // post process all processed models
        allProcessedModels = config.postProcessAllModels(allProcessedModels);

        // generate files based on processed models
        for (String modelName: allProcessedModels.keySet()) {
            Map<String, Object> models = (Map<String, Object>)allProcessedModels.get(modelName);
            Map<String, Object> modelTemplate = (Map<String, Object>) ((List<Object>) models.get("models")).get(0);
            allModels.put(modelName, (CodegenModel) modelTemplate.get("model"));
        }
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
        for(String s: importSet) {
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
        for(String s: importSet) {
            Map<String, String> item = new HashMap<>();
            item.put("import", s);
            imports.add(item);
        }
        objs.put("imports", imports);
        config.postProcessModels(objs);
        return objs;
    }
}
