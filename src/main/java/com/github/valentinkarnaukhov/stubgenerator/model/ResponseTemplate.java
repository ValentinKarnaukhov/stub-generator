package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseTemplate {

    private String responseType;
    private String code;
    private String description;
    private List<ResponseField> responseSetters;

    @Getter
    @Builder
    public static class ResponseField{
        private String responseFieldName;
        private String responseFieldType;
        private String methodResponseFieldName;
        private String setterName;
    }
}
