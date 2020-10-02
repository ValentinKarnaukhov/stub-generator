package com.github.valentinkarnaukhov.stubgenerator.model.adapter;

import io.swagger.codegen.v3.CodegenModel;

public class CodegenParameter {

    private boolean isListContainer;
    private boolean isCollection;
    private boolean isPrimitiveType;
    private String baseType;
    private String dataType;
    private String setter;
    private String getter;
    private String value;

    public CodegenParameter fromModel(CodegenModel codegenModel){
        return null;
    }
}
