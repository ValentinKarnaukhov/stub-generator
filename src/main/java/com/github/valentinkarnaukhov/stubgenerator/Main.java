package com.github.valentinkarnaukhov.stubgenerator;

import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.util.InlineModelResolver;

public class Main {

    private static String inputSpec = "swagger_2_0.yaml";


    public static void main(String[] args) {
        CodegenConfigurator codegenConfigurator = new CodegenConfigurator();

        codegenConfigurator.setModelPackage("com.github.valentinkarnaukhov.stubgenerator.model");
        codegenConfigurator.setLang("java");
        codegenConfigurator.setInputSpec("src/test/resources/query_params_swagger.yaml");
        codegenConfigurator.setOutputDir("target/generated-sources/swagger");
        codegenConfigurator.setTemplateDir("src/main/resources");

        ClientOptInput input = codegenConfigurator.toClientOptInput();

        OpenAPI openAPI = new OpenAPIV3Parser().read(codegenConfigurator.getInputSpec());
        InlineModelResolver resolver = new InlineModelResolver(true, true);
        resolver.flatten(openAPI);
        input.setOpenAPI(openAPI);

        WiremockGenerator wiremockGenerator = new WiremockGenerator();
        wiremockGenerator.setGeneratorPropertyDefault("explode", "true");

        wiremockGenerator.opts(input);
        wiremockGenerator.setGeneratorPropertyDefault("maxDepth", "0");
//        wiremockGenerator.setGeneratorPropertyDefault("delegateObject", "com.github.valentinkarnaukhov.stubgenerator.TestMock");
        wiremockGenerator.generate();
    }


}
