package com.github.valentinkarnaukhov.stubgenerator;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import com.github.valentinkarnaukhov.stubgenerator.Parser;

import java.io.IOException;

public class Main {

    private static String inputSpec = "swagger_2_0.yaml";


    public static void main(String[] args) {
        OpenAPI openAPI = new OpenAPIV3Parser().read(inputSpec);
        System.out.println("###############");

        WiremockGenerator wiremockGenerator = new WiremockGenerator(openAPI);
        wiremockGenerator.generate();


    }



}
