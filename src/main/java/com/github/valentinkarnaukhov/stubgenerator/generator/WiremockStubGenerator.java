package com.github.valentinkarnaukhov.stubgenerator.generator;

import com.github.valentinkarnaukhov.stubgenerator.model.CodegenConfiguration;
import com.github.valentinkarnaukhov.stubgenerator.model.GeneratorProperties;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import com.github.valentinkarnaukhov.stubgenerator.parser.JavaSpecParser;
import com.github.valentinkarnaukhov.stubgenerator.parser.SpecParser;
import io.swagger.codegen.v3.AbstractGenerator;
import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.CodegenConfig;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.util.InlineModelResolver;
import lombok.Setter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.valentinkarnaukhov.stubgenerator.util.Util.validateNotNull;

/**
 * @author Valentin Karnaukhov
 */
public class WiremockStubGenerator extends AbstractGenerator implements Generator {

    private OpenAPI openAPI;
    private CodegenConfig config;
    private String stubPackage = null;
    private String supportPackage = null;
    private Boolean generateModels = null;
    private Boolean explode = null;
    private Integer maxDepth = null;
    private Boolean useTags = null;
    @Setter
    private Map<String, String> prefixMap = new HashMap<>();
    private GeneratorProperties properties;

    @Override
    public void configure(CodegenConfiguration configuration) {
        CodegenConfigurator codegenConfigurator = new CodegenConfigurator();

        codegenConfigurator.setModelPackage(configuration.getModelPackage());
        codegenConfigurator.setLang(configuration.getLang());
        codegenConfigurator.setInputSpec(configuration.getInputSpec());
        codegenConfigurator.setOutputDir(configuration.getOutputDir());
        this.generateModels = configuration.getGenerateModels();
        this.stubPackage = configuration.getStubPackage();
        this.supportPackage = configuration.getSupportPackage();
        this.maxDepth = configuration.getGeneratorProperties().getMaxDepth();
        this.explode = configuration.getGeneratorProperties().getExplode();
        this.useTags = configuration.getGeneratorProperties().getUseTags();
        this.properties = configuration.getGeneratorProperties();

        if (configuration.getModelPackage() == null) {
            codegenConfigurator.setModelPackage("com.github.valentinkarnaukhov.stubgenerator.model");
        }

        if (configuration.getLang() == null) {
            codegenConfigurator.setLang("java");
        }

        if (configuration.getInputSpec() == null) {
            codegenConfigurator.setInputSpec(configuration.getInputSpec());
        }

        if (configuration.getOutputDir() == null) {
            codegenConfigurator.setOutputDir(configuration.getOutputDir());
        }

        if (configuration.getGenerateModels() == null) {
            this.generateModels = true;
        }

        if (configuration.getStubPackage() == null) {
            this.stubPackage = "com.github.valentinkarnaukhov.stubgenerator.stub";
        }

        if (configuration.getSupportPackage() == null) {
            this.supportPackage = this.stubPackage + ".support";
        }

        if (configuration.getGeneratorProperties().getMaxDepth() == null) {
            this.maxDepth = 3;
        }

        if (configuration.getGeneratorProperties().getExplode() == null) {
            this.explode = false;
        }

        if (configuration.getGeneratorProperties().getUseTags() == null) {
            this.useTags = false;
        }

        if (configuration.getGeneratorProperties().getPrefixMap() == null) {
            this.prefixMap = new HashMap<>();
        }

        if (!explode) {
            maxDepth = 0;
        }

        ClientOptInput input = codegenConfigurator.toClientOptInput();
        OpenAPI openAPI = new OpenAPIV3Parser().read(configuration.getInputSpec());
        InlineModelResolver resolver = new InlineModelResolver(true, true);
        resolver.flatten(openAPI);
        input.setOpenAPI(openAPI);
        this.opts(input);
        this.validate();
    }

    @Override
    public io.swagger.codegen.v3.Generator opts(ClientOptInput opts) {
        this.openAPI = opts.getOpenAPI();
        this.config = opts.getConfig();
        this.config.processOpts();
        return this;
    }

    @Override
    public List<File> generate() {
        SpecParser parser = new JavaSpecParser(config, properties);
        List<TagTemplate> templateData = parser.parse(this.openAPI);

        return null;
    }

    @Override
    public void validate() {
        validateNotNull(config.modelPackage());
        validateNotNull(config.getInputSpec());
        validateNotNull(config.outputFolder());
        validateNotNull(stubPackage);
        validateNotNull(supportPackage);
        validateNotNull(generateModels);
        validateNotNull(explode);
        validateNotNull(maxDepth);
        validateNotNull(useTags);
        validateNotNull(prefixMap);
    }
}
