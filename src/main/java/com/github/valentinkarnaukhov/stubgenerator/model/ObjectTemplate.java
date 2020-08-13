package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ObjectTemplate {

    private String objectType;
    private String code;
    private String description;
    private List<FieldTemplate> fields;
}
