package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TagTemplate {

    private String tag;
    private List<PathTemplate> paths;
}
