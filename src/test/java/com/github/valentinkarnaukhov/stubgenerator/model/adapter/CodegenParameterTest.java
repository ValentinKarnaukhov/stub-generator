package com.github.valentinkarnaukhov.stubgenerator.model.adapter;

import io.swagger.codegen.v3.CodegenConfig;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.generators.openapi.OpenAPIGenerator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.util.InlineModelResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Valentin Karnaukhov
 */
class CodegenParameterTest {

    private static OpenAPI openAPI;
    private CodegenConfig config = new OpenAPIGenerator();

    @BeforeAll
    static void setUp() {
        openAPI = new OpenAPIV3Parser().read("parameter_adapter.yaml");
        InlineModelResolver resolver = new InlineModelResolver(true, true);
        resolver.flatten(openAPI);
    }

    @Test
    void fromModel() {
    }

    @Test
    void fromProperty() {
    }

    @Test
    void fromParameter() {
        Operation operation = openAPI.getPaths().get("/string").getGet();
        CodegenOperation cgOperation = config.fromOperation("/string", "GET", operation, openAPI.getComponents().getSchemas(), openAPI);

        io.swagger.codegen.v3.CodegenParameter source = cgOperation.getQueryParams().get(0);
        CodegenParameter target = CodegenParameter.fromParameter(source);
        Assertions.assertEquals(, target.isCollection());
        Assertions.assertEquals(, target.isPrimitiveType());
        Assertions.assertEquals(, target.getName());
        Assertions.assertEquals(, target.getBaseType());
    }

    @Test
    void fromResponse() {
    }
}