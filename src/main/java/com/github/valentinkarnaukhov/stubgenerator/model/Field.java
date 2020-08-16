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
    private String wayToParent;
    private String compositeName;

}
