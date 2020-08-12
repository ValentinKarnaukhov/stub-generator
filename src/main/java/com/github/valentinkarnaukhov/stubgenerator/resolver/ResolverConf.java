package com.github.valentinkarnaukhov.stubgenerator.resolver;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResolverConf {

    private final String fieldNamePrefix, fieldNameSuffix, fieldNameDelimiter;
    private final String wayToObjPrefix, wayToObjSuffix, wayToObjDelimiter;

}
