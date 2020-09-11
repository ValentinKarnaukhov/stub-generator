package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GeneratorProperties {

    private Boolean explode;
    private Boolean useTags;
    private Map<String, String> prefixMap = new HashMap<>();
    private Integer maxDepth;

}
