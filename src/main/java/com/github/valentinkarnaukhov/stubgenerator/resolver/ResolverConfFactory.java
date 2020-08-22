package com.github.valentinkarnaukhov.stubgenerator.resolver;

import io.swagger.codegen.v3.CodegenProperty;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResolverConfFactory {

    public static ResolverConf getForQuery() {
        ResolverConf conf = ResolverConf.builder()
                .compNamePrefix("inQuery")
                .compNameDelimiter("_")
                .compNameSuffix("")
                .build();
        Function<ModelResolver.Node, String> compositeNameFunction =
                n -> Stream.concat(n.getWay().stream(), Stream.of(n.getSourceProperty()))
                        .map(CodegenProperty::getNameInCamelCase)
                        .collect(Collectors.joining(conf.getCompNameDelimiter(), conf.getCompNamePrefix(), conf.getCompNameSuffix()));
        conf.setCompositeNameFunction(compositeNameFunction);
        return conf;
    }

    public static ResolverConf getForResponse() {
        ResolverConf conf = getForBody();
        conf.setCompNamePrefix("inResp");
        return conf;
    }

    public static ResolverConf getForBody() {
        ResolverConf conf = ResolverConf.builder()
                .compNamePrefix("inBody")
                .compNameDelimiter("_")
                .compNameSuffix("")
                .wayToParentDelimiter("().")
                .wayToParentPrefix(".")
                .wayToParentSuffix("()")
                .build();
        Function<ModelResolver.Node, String> compositeNameFunction =
                n -> Stream.concat(n.getWay().stream(), Stream.of(n.getSourceProperty()))
                        .map(CodegenProperty::getNameInCamelCase)
                        .collect(Collectors.joining(conf.getCompNameDelimiter(), conf.getCompNamePrefix(), conf.getCompNameSuffix()));
        Function<ModelResolver.Node, String> wayToParentFunction =
                n -> {
                    String res = n.getWay().stream()
                            .map(CodegenProperty::getGetter)
                            .collect(Collectors.joining(conf.getWayToParentDelimiter(), conf.getWayToParentPrefix(), conf.getWayToParentSuffix()));
                    return res.equals(".()") ? "" : res;
                };
        Function<ModelResolver.Node, String> parentSetterFunction =
                n -> {
                    String res = n.getWay().stream()
                            .limit(n.getWay().isEmpty() ? 0 : n.getWay().size() - 1)
                            .map(CodegenProperty::getGetter)
                            .collect(Collectors.joining(conf.getWayToParentDelimiter(), conf.getWayToParentPrefix(), conf.getWayToParentSuffix()));
                    res = res.equals(".()") ? "" : res;
                    return n.getWay().isEmpty() ? res : res + conf.getWayToParentPrefix() + n.getWay().get(n.getWay().size() - 1).getSetter();
                };
        Function<ModelResolver.Node, String> jsonPathFunction =
                n -> {
                    String res = n.getWay().stream()
                            .map(CodegenProperty::getName)
                            .collect(Collectors.joining(".","$.",""));
                    return res.equals(".()") ? "" : res;
                };
        conf.setCompositeNameFunction(compositeNameFunction);
        conf.setWayToParentFunction(wayToParentFunction);
        conf.setParentSetterFunction(parentSetterFunction);
        conf.setJsonPathFunction(jsonPathFunction);
        return conf;
    }
}
