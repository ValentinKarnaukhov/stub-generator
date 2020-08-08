package com.github.valentinkarnaukhov.stubgenerator;

import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.ClientOpts;
import io.swagger.codegen.v3.CodegenConfig;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.codegen.v3.generators.DefaultCodegenConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import com.github.valentinkarnaukhov.stubgenerator.Parser;

import java.io.IOException;
import java.util.HashMap;

public class Main {

    private static String inputSpec = "swagger_2_0.yaml";


    public static void main(String[] args) {
        OpenAPI openAPI = new OpenAPIV3Parser().read(inputSpec);
        System.out.println("###############");

        CodegenConfigurator codegenConfigurator = new CodegenConfigurator();

        codegenConfigurator.setModelPackage("com.github.valentinkarnaukhov.stubgenerator.model");
        codegenConfigurator.setLang("java");
        codegenConfigurator.setInputSpec("stub-generator/src/main/resources/swagger_2_0.yaml");
        codegenConfigurator.

        ClientOptInput input = codegenConfigurator.toClientOptInput();
        input.setOpenAPI(openAPI);

        DefaultGenerator defaultGenerator = new DefaultGenerator();

        defaultGenerator.opts(input);
        defaultGenerator.generate();

//        WiremockGenerator wiremockGenerator = new WiremockGenerator();
//        wiremockGenerator.opts(input);
//        wiremockGenerator.generate();
    }



}
