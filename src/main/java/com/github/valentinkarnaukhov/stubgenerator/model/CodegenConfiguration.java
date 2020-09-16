package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodegenConfiguration {

    private String modelPackage;
    private String lang;
    private String inputSpec;
    private String outputDir;
    private Boolean generateModels;
    private String stubPackage;
    private String supportPackage;
    private String delegateObject;

    private GeneratorProperties generatorProperties = new GeneratorProperties();

}
