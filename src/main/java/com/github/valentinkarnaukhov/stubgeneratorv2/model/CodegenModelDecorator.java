package com.github.valentinkarnaukhov.stubgeneratorv2.model;

import io.swagger.codegen.v3.CodegenModel;

/**
 * @author Valentin Karnaukhov
 */
public class CodegenModelDecorator implements Item{

    private final CodegenModel codegenModel;

    public CodegenModelDecorator(CodegenModel codegenModel) {
        this.codegenModel = codegenModel;
    }

    @Override
    public String getType() {
        return codegenModel.getDataType();
    }

    @Override
    public boolean isPrimitive() {
        return codegenModel.getIsPrimitiveType();
    }
}
