package com.github.valentinkarnaukhov.stubgenerator.resolver;

import com.github.valentinkarnaukhov.stubgenerator.model.FieldTemplate;
import io.swagger.codegen.v3.CodegenProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BiFunction;
import java.util.function.Function;

@Builder
@Getter
@Setter
public class ResolverConf {

    private final String compNamePrefix, compNameSuffix, compNameDelimiter;
    private final String wayToObjPrefix, wayToObjSuffix, wayToObjDelimiter;
    private BiFunction<CodegenProperty, ModelResolver.Node, FieldTemplate> propertyToFields;
    private Function<ModelResolver.Node, String> compositeNameFunction;

}
