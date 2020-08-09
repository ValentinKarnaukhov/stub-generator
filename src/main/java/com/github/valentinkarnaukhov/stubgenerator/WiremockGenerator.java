package com.github.valentinkarnaukhov.stubgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import com.github.valentinkarnaukhov.stubgenerator.util.ModelResolver;
import io.swagger.codegen.v3.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class WiremockGenerator extends AbstractGenerator implements Generator {

    private ObjectMapper objectMapper;
    private CodegenConfig config;
    private OpenAPI openAPI;
    private Parser parser;
    private Map<String, CodegenModel> allModels;
    private String stubPackage = null;
    private Boolean generateModels = null;
    private Boolean generateStub = null;
    private final Map<String, String> generatorPropertyDefaults = new HashMap<>();
    private final Map<String, String> importPackages = new HashMap<>();

    @Override
    public List<File> generate() {
        configureGeneratorProperties();
        generateModels(this.allModels);

        ModelResolver modelResolver = new ModelResolver(this.allModels, 3);
        ModelResolver.Node node = modelResolver.resolveFlatten(this.allModels.get("TestObject"), 0);

        if (!generateStub) {
            return null;
        }
        List<TagTemplate> templates = parser.parse();
        templates.stream()
                .peek(this::preProcessTemplate)
                .forEach(this::processTemplate);
        return null;
    }

    public void setGeneratorPropertyDefault(final String key, final String value) {
        this.generatorPropertyDefaults.put(key, value);
    }

    private void preProcessTemplate(TagTemplate tagTemplate) {
        tagTemplate.setImportPackages(this.importPackages);
    }

    private void configureGeneratorProperties() {
        if (generatorPropertyDefaults.containsKey("generateModels")) {
            this.generateModels = Boolean.valueOf(generatorPropertyDefaults.get("generateModels"));
        }

        if (generatorPropertyDefaults.containsKey("generateStub")) {
            this.generateStub = Boolean.valueOf(generatorPropertyDefaults.get("generateStub"));
        }

        if (generatorPropertyDefaults.containsKey("stubPackage")) {
            this.stubPackage = generatorPropertyDefaults.get("stubPackage");
        }

        customOrDefault();

        importPackages.put("stubPackage", stubPackage);
        importPackages.put("modelPackage", config.modelPackage());

        if (generatorPropertyDefaults.containsKey("delegateObject")) {
            importPackages.put("delegateObject", generatorPropertyDefaults.get("delegateObject"));
        }
    }

    private void customOrDefault() {
        if (generateModels == null) {
            generateModels = true;
        }

        if (generateStub == null) {
            generateStub = true;
        }

        if (stubPackage == null) {
            stubPackage = "com.github.valentinkarnaukhov.stubgenerator.stub";
        }
    }

    private void processTemplate(TagTemplate tagTemplate) {
        try {
            String outputFilename = config.getOutputDir() + File.separator + "src/main/java" + File.separator +
                    stubPackage.replace('.', '/') + File.separator + tagTemplate.getTag() + ".java";
            Map<String, Object> templateData = this.objectMapper.convertValue(tagTemplate, Map.class);
            processTemplateToFile(templateData, "TagTemplate.mustache", outputFilename);
        } catch (Exception e) {
            throw new RuntimeException("Can't process data to file");
        }
    }

    @Override
    public Generator opts(ClientOptInput opts) {
        this.openAPI = opts.getOpenAPI();
        this.config = opts.getConfig();
        this.parser = new Parser(this);
        this.allModels = new HashMap<>();
        this.config.processOpts();
        this.objectMapper = new ObjectMapper();
        return this;
    }

    private void generateModels(Map<String, CodegenModel> allModels) {
        final Map<String, Schema> schemas = this.openAPI.getComponents().getSchemas();
        if (schemas == null) {
            return;
        }

        Set<String> modelKeys = schemas.keySet();

        // store all processed models
        Map<String, Object> allProcessedModels = new TreeMap<>((o1, o2) -> ObjectUtils.compare(config.toModelName(o1), config.toModelName(o2)));

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

                final List<Object> modelList = (List<Object>) models.get("models");

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
        for (String modelName : allProcessedModels.keySet()) {
            Map<String, Object> models = (Map<String, Object>) allProcessedModels.get(modelName);
            try {
                Map<String, Object> modelTemplate = (Map<String, Object>) ((List<Object>) models.get("models")).get(0);
                allModels.put(modelName, (CodegenModel) modelTemplate.get("model"));

                if (!generateModels) {
                    continue;
                }
                for (String templateName : config.modelTemplateFiles().keySet()) {
                    String suffix = config.modelTemplateFiles().get(templateName);
                    String filename = config.modelFileFolder() + File.separator + config.toModelFilename(modelName) + suffix;
                    processTemplateToFile(models, templateName, filename);
                }
            } catch (Exception e) {
                throw new RuntimeException("Could not generate model '" + modelName + "'", e);
            }
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

    private File processTemplateToFile(Map<String, Object> templateData, String templateName, String outputFilename) throws IOException {
        String adjustedOutputFilename = outputFilename.replaceAll("//", "/").replace('/', File.separatorChar);
        String templateFile = getFullTemplateFile(config, templateName);
        String rendered = config.getTemplateEngine().getRendered(templateFile, templateData);
        writeToFile(adjustedOutputFilename, rendered);
        return new File(adjustedOutputFilename);
    }
}
