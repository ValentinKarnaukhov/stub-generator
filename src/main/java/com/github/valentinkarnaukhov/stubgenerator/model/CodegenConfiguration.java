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
    private String templateDir;
    private Boolean generateModels;
    private Boolean generateStub = true;
    private String stubPackage;
    private String delegateObject;

    private GeneratorProperties generatorProperties = new GeneratorProperties();

}
