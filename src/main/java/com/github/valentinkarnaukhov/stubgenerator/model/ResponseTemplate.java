package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseTemplate {

    private String code;
    private String description;
    private ObjectTemplate response;

    public ResponseTemplate(ObjectTemplate objectTemplate){
        this.response = objectTemplate;
    }
}
