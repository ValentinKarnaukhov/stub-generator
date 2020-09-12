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

        ClientOptInput input = codegenConfigurator.toClientOptInput();

        OpenAPI openAPI = new OpenAPIV3Parser().read(codegenConfigurator.getInputSpec());
        InlineModelResolver resolver = new InlineModelResolver(true, true);
        resolver.flatten(openAPI);
        input.setOpenAPI(openAPI);

        WiremockGenerator wiremockGenerator = new WiremockGenerator();
        setPropertyIfNotNull("explode", properties.getExplode(), wiremockGenerator);
        setPropertyIfNotNull("useTags", properties.getUseTags(), wiremockGenerator);
        setPropertyIfNotNull("maxDepth", properties.getMaxDepth(), wiremockGenerator);

        wiremockGenerator.opts(input);
        wiremockGenerator.generate();
    }

    private void setPropertyIfNotNull(String key, Object object, WiremockGenerator generator) {
        if (Objects.nonNull(object)) {
            generator.setGeneratorPropertyDefault(key, object.toString());
        }
    }

}
