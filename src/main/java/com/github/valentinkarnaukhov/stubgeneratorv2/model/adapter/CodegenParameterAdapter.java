package com.github.valentinkarnaukhov.stubgeneratorv2.model.adapter;

import com.github.valentinkarnaukhov.stubgeneratorv2.model.Item;
import io.swagger.codegen.v3.CodegenParameter;

import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public class CodegenParameterAdapter extends CodegenParameter implements Item {

    private final CodegenParameter codegenParameter;

    public CodegenParameterAdapter(CodegenParameter codegenParameter) {
        this.codegenParameter = codegenParameter;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getSetter() {
        return null;
    }

    @Override
    public String getGetter() {
        return null;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getCollectionType() {
        return null;
    }

    @Override
    public List<Item> getFields() {
        return null;
    }
}
