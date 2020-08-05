package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TagTemplate {

    private String tag;
    private List<PathTemplate> paths;
}
