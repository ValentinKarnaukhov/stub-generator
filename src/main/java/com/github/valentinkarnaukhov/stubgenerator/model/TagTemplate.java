package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TagTemplate {

    private String tag;
    private Map<String, Object> importPackages;
    private List<PathTemplate> paths;

    public TagTemplate(String tag, List<PathTemplate> paths) {
        this.tag = tag;
        this.paths = paths;
    }
}
