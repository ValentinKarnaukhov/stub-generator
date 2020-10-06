package com.github.valentinkarnaukhov.stubgenerator.parser;

import com.github.valentinkarnaukhov.stubgenerator.model.PathTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

public interface SpecParser {

    List<TagTemplate> parse(OpenAPI spec, List<CodegenParameter> allModels);
    PathTemplate operationToPath(CodegenOperation operation);
}
