package com.github.valentinkarnaukhov.stubgeneratorv2.model.adapter;

import com.github.valentinkarnaukhov.stubgeneratorv2.model.Item;
import io.swagger.codegen.v3.CodegenProperty;

import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public class CodegenPropertyAdapter extends CodegenProperty implements Item {

    private final CodegenProperty codegenProperty;

    public CodegenPropertyAdapter(CodegenProperty codegenProperty) {
        this.codegenProperty = codegenProperty;
    }

    @Override
    public String getType() {
        return codegenProperty.getDatatype();
    }

    @Override
    public String getName() {
        return codegenProperty.getName();
    }

    @Override
    public String getSetter() {
        return codegenProperty.getGetter();
    }

    @Override
    public String getGetter() {
        return codegenProperty.getGetter();
    }

    @Override
    public boolean isPrimitive() {
        return codegenProperty.getIsPrimitiveType();
    }

    @Override
    public boolean isCollection() {
        return codegenProperty.getIsListContainer();
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
