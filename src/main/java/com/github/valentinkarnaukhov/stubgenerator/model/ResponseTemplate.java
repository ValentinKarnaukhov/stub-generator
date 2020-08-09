package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseTemplate {

    private String responseType;
    private String code;
    private String description;
    private List<FieldTemplate> responseField;
}
