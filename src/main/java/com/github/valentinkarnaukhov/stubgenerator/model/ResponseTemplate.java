package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseTemplate {

    private String code;
    private String description;
    private ObjectTemplate response;
}
