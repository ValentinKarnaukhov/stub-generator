package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FieldTemplate {
    
    private String fieldName;
    private String fieldType;
    private String methodFieldName;
    private String setterName;
    private String wayToObject;
    private String compositeFieldName;
}
