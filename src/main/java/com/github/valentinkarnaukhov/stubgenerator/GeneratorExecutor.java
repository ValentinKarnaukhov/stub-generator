package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.model.CodegenConfiguration;
import com.github.valentinkarnaukhov.stubgenerator.model.GeneratorProperties;
import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.util.InlineModelResolver;

import java.util.Objects;

public class GeneratorExecutor {

    public void generate(CodegenConfiguration configuration) {
        CodegenConfigurator codegenConfigurator = new CodegenConfigurator();
        GeneratorProperties properties = configuration.getGeneratorProperties();

        codegenConfigurator.setModelPackage(configuration.getModelPackage());
        codegenConfigurator.setLang(configuration.getLang());
        codegenConfigurator.setInputSpec(configuration.getInputSpec());
        codegenConfigurator.setOutputDir(configuration.getOutputDir());

        codegenConfigurator.setTemplateDir("/Users/valentin/IdeaProjects/stub-generator/src/main/resources");

        ClientOptInput input = codegenConfigurator.toClientOptInput();

        OpenAPI openAPI = new OpenAPIV3Parser().read(codegenConfigurator.getInputSpec());
        InlineModelResolver resolver = new InlineModelResolver(true, true);
        resolver.flatten(openAPI);
        input.setOpenAPI(openAPI);

        WiremockGenerator wiremockGenerator = new WiremockGenerator();
        wiremockGenerator.setGeneratorPropertyDefault("explode", nullOrToString(properties.getExplode()));
        wiremockGenerator.setGeneratorPropertyDefault("useTags", nullOrToString(properties.getUseTags()));

        wiremockGenerator.setPrefixMap(properties.getPrefixMap());
        wiremockGenerator.setGeneratorPropertyDefault("maxDepth", nullOrToString(properties.getMaxDepth()));

        wiremockGenerator.opts(input);
        wiremockGenerator.generate();
    }

    private String nullOrToString(Object object) {
        return Objects.nonNull(object) ? object.toString() : null;
    }

}
