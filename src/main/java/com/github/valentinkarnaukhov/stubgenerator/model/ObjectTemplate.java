package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ObjectTemplate {

    private boolean isPrimitive = true;
    private boolean isCollection = false;
    private String type;
    private String name;
    private String camelizedName;
    private String prefix;
    private List<Field> fields = new ArrayList<>();

    public Field toField(){
        Field field = new Field();
        field.setPrimitive(isPrimitive);
        field.setCollection(isCollection);
        field.setType(type);
        return field;
    }
}
