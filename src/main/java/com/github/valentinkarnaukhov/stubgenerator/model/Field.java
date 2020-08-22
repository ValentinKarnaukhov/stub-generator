package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "type")
public class Field {

    private boolean isPrimitive = true;
    private boolean isCollection = false;
    private String type;
    private String name;
    private String setter;
    private String getter;
    private String wayToParent;
    private String compositeName;
    private String value;
    private String baseType;
    private String parentType;
    private String parentSetter;
    private String jsonPath;

    public ObjectTemplate toObjectTemplate(){
        ObjectTemplate objectTemplate = new ObjectTemplate();
        objectTemplate.setPrimitive(isPrimitive);
        objectTemplate.setCollection(isCollection);
        objectTemplate.setType(type);
        objectTemplate.setBaseType(baseType);
        return objectTemplate;
    }

}
