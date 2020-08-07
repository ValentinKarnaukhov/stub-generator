package com.github.valentinkarnaukhov.stubgenerator;

import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.ClientOpts;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.generators.DefaultCodegenConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import com.github.valentinkarnaukhov.stubgenerator.Parser;

import java.io.IOException;

public class Main {

    private static String inputSpec = "swagger_2_0.yaml";


    public static void main(String[] args) {
        OpenAPI openAPI = new OpenAPIV3Parser().read(inputSpec);
        System.out.println("###############");

        DefaultCodegenConfig defaultCodegenConfig = new WiremockCodegenConfig();
//        defaultCodegenConfig.setModelPackage("com.github.valentinkarnaukhov.stubgenerator.model");
        defaultCodegenConfig.setOutputDir("./target/generated-sources/wiremockgenerator/");

        WiremockGenerator wiremockGenerator = new WiremockGenerator(openAPI, defaultCodegenConfig);
        DefaultGenerator defaultGenerator = new DefaultGenerator();

        ClientOptInput input = new ClientOptInput();
        input.setOpenAPI(openAPI);
        input.setConfig(defaultCodegenConfig);
        input.setOpts(new ClientOpts());

        defaultGenerator.opts(input);
        defaultGenerator.generate();
//        wiremockGenerator.generate();


    }



}
