package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TagTemplate {

    private String tag;
    private String modelPackage;
    private List<PathTemplate> paths;

    public TagTemplate(String tag, List<PathTemplate> paths) {
        this.tag = tag;
        this.paths = paths;
    }
}
