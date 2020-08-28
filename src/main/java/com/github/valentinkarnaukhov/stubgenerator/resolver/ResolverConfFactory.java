package com.github.valentinkarnaukhov.stubgenerator.resolver;

import com.github.valentinkarnaukhov.stubgenerator.WiremockGenerator;
import io.swagger.codegen.v3.CodegenProperty;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResolverConfFactory {

    public static ResolverConf getForQuery(WiremockGenerator generator) {
        ResolverConf conf = ResolverConf.builder()
                .compNamePrefix(generator.getPrefixMap().getOrDefault("query", "inQuery"))
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

    public static ResolverConf getForResponse(WiremockGenerator generator) {
        ResolverConf conf = getForBody(generator);
        conf.setCompNamePrefix(generator.getPrefixMap().getOrDefault("response", "inResp"));
        return conf;
    }

    public static ResolverConf getForBody(WiremockGenerator generator) {
        ResolverConf conf = ResolverConf.builder()
                .compNamePrefix(generator.getPrefixMap().getOrDefault("body", "inBody"))
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
                    return res.equals(conf.getWayToParentPrefix() + conf.getWayToParentSuffix()) ? "" : res;
                };
        Function<ModelResolver.Node, String> parentSetterFunction =
                n -> {
                    String res = n.getWay().stream()
                            .limit(n.getWay().isEmpty() ? 0 : n.getWay().size() - 1)
                            .map(CodegenProperty::getGetter)
                            .collect(Collectors.joining(conf.getWayToParentDelimiter(), conf.getWayToParentPrefix(), conf.getWayToParentSuffix()));
                    res = res.equals(conf.getWayToParentPrefix() + conf.getWayToParentSuffix()) ? "" : res;
                    return n.getWay().isEmpty() ? res : res + conf.getWayToParentPrefix() + n.getWay().get(n.getWay().size() - 1).getSetter();
                };

        conf.setCompositeNameFunction(compositeNameFunction);
        conf.setWayToParentFunction(wayToParentFunction);
        conf.setParentSetterFunction(parentSetterFunction);
        return conf;
    }
}
