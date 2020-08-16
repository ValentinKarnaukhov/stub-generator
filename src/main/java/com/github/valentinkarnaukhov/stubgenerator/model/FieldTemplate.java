package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "baseType")
public class FieldTemplate {

    private Boolean isCollection;
    private Boolean isPrimitive;
    private String fieldName;
    private String baseType;
    private String fieldType;
    private String methodFieldName;
    private String setterName;
    private String wayToObject;
    private String compositeFieldName;
}
