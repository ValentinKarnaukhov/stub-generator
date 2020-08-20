package com.github.valentinkarnaukhov.stubgenerator.resolver;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Builder
@Getter
@Setter
public class ResolverConf {

    private final String compNamePrefix, compNameSuffix, compNameDelimiter;
    private final String wayToParentPrefix, wayToParentSuffix, wayToParentDelimiter;
    private Function<ModelResolver.Node, String> compositeNameFunction;
    private Function<ModelResolver.Node, String> wayToParentFunction;
    private Function<ModelResolver.Node, String> parentSetterFunction;

}
