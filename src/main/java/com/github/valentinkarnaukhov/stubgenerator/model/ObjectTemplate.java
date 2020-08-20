package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "type")
public class ObjectTemplate {

    private boolean isPrimitive = true;
    private boolean isCollection = false;
    private String type;
    private String name;
    private String camelizedName;
    private String prefix;
    private String value;
    private String baseType;
    private List<Field> fields = new ArrayList<>();
}
