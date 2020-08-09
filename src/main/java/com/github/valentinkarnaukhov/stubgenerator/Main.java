package com.github.valentinkarnaukhov.stubgenerator;

import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class Main {

    private static String inputSpec = "swagger_2_0.yaml";


    public static void main(String[] args) {
        OpenAPI openAPI = new OpenAPIV3Parser().read(inputSpec);
        System.out.println("###############");

        CodegenConfigurator codegenConfigurator = new CodegenConfigurator();

        codegenConfigurator.setModelPackage("com.github.valentinkarnaukhov.stubgenerator.model");
        codegenConfigurator.setLang("java");
        codegenConfigurator.setInputSpec("src/main/resources/swagger_2_0.yaml");
        codegenConfigurator.setOutputDir("target/generated-sources/swagger");
        codegenConfigurator.setTemplateDir("src/main/resources");

        ClientOptInput input = codegenConfigurator.toClientOptInput();
        input.setOpenAPI(openAPI);

        WiremockGenerator wiremockGenerator = new WiremockGenerator();
        wiremockGenerator.opts(input);
//        wiremockGenerator.setGeneratorPropertyDefault("delegateObject", "com.github.valentinkarnaukhov.stubgenerator.TestMock");
        wiremockGenerator.generate();
    }



}
