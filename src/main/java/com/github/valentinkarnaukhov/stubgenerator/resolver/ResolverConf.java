package com.github.valentinkarnaukhov.stubgenerator.resolver;

import com.github.valentinkarnaukhov.stubgenerator.model.FieldTemplate;
import io.swagger.codegen.v3.CodegenProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BiFunction;

@Builder
@Getter
@Setter
public class ResolverConf {

    private final String fieldNamePrefix, fieldNameSuffix, fieldNameDelimiter;
    private final String wayToObjPrefix, wayToObjSuffix, wayToObjDelimiter;
    private BiFunction<CodegenProperty, ModelResolver.Node, FieldTemplate> propertyToFields;

}
