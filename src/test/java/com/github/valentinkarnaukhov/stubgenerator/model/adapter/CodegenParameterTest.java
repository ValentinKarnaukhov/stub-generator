package com.github.valentinkarnaukhov.stubgenerator.model.adapter;

import io.swagger.codegen.v3.CodegenConfig;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenResponse;
import io.swagger.codegen.v3.generators.openapi.OpenAPIGenerator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.util.InlineModelResolver;
import org.junit.jupiter.api.BeforeAll;
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
    void fromNonCollectionParameter() {
        Operation operation = openAPI.getPaths().get("/codegenParameterTest").getGet();
        CodegenOperation cgOperation = config.fromOperation("/codegenParameterTest", "GET", operation, openAPI.getComponents().getSchemas(), openAPI);

        io.swagger.codegen.v3.CodegenParameter source = cgOperation.getQueryParams().get(0);
        CodegenParameter target = CodegenParameter.fromParameter(source);

        assertFalse(target.isCollection());
        assertTrue(target.isPrimitiveType());
        assertEquals("stringParam", target.getName());
        assertEquals("String", target.getBaseType());
        assertEquals("String", target.getType());
        assertNull(target.getSetter());
        assertNull(target.getGetter());
        assertNull(target.getValue());
        assertNull(target.getAllVars());
    }

    @Test
    void fromCollectionParameter() {
        Operation operation = openAPI.getPaths().get("/codegenParameterTest").getGet();
        CodegenOperation cgOperation = config.fromOperation("/codegenParameterTest", "GET", operation, openAPI.getComponents().getSchemas(), openAPI);

        io.swagger.codegen.v3.CodegenParameter source = cgOperation.getQueryParams().get(1);
        CodegenParameter target = CodegenParameter.fromParameter(source);

        assertTrue(target.isCollection());
        assertFalse(target.isPrimitiveType());
        assertEquals("stringListParam", target.getName());
        assertEquals("String", target.getBaseType());
        assertEquals("List", target.getType());
        assertNull(target.getSetter());
        assertNull(target.getGetter());
        assertNull(target.getValue());
        assertNull(target.getAllVars());
    }

    @Test
    void fromResponse() {
        Operation operation = openAPI.getPaths().get("/codegenParameterTest").getGet();
        CodegenOperation cgOperation = config.fromOperation("/codegenParameterTest", "GET", operation, openAPI.getComponents().getSchemas(), openAPI);

        CodegenResponse source = cgOperation.getResponses().get(0);
        CodegenParameter target = CodegenParameter.fromResponse(source);

        assertFalse(target.isCollection());
        assertTrue(target.isPrimitiveType());
        assertNull(target.getName());
        assertEquals("String", target.getBaseType());
        assertEquals("String", target.getType());
        assertNull(target.getSetter());
        assertNull(target.getGetter());
        assertNull(target.getValue());
        assertNull(target.getAllVars());
    }
}