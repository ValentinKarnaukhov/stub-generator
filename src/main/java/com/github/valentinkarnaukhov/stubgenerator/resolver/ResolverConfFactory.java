package com.github.valentinkarnaukhov.stubgenerator.resolver;

public class ResolverConfFactory {

    public static ResolverConf getForQuery() {
        return getForResponse();
    }

    public static ResolverConf getForResponse() {
        return ResolverConf.builder()
                .fieldNamePrefix("")
                .fieldNameDelimiter("_")
                .fieldNameSuffix("")
                .wayToObjPrefix(".")
                .wayToObjDelimiter("().")
                .wayToObjSuffix("()")
                .build();
    }

    public static ResolverConf getForBody() {
        return ResolverConf.builder()
                .fieldNamePrefix("")
                .fieldNameDelimiter("_")
                .fieldNameSuffix("")
                .wayToObjPrefix("$.")
                .wayToObjDelimiter(".")
                .wayToObjSuffix("")
                .build();
    }
}
