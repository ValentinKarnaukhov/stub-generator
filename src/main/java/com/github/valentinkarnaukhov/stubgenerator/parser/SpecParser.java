package com.github.valentinkarnaukhov.stubgenerator.parser;

import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import com.github.valentinkarnaukhov.stubgenerator.resolver.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

public interface SpecParser {

    List<TagTemplate> parse(OpenAPI spec);
}
