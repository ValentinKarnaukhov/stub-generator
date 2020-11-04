package com.github.valentinkarnaukhov.stubgeneratorv2.model.adapter;

import com.github.valentinkarnaukhov.stubgeneratorv2.model.Item;
import io.swagger.codegen.v3.CodegenModel;

import java.util.List;


/**
 * @author Valentin Karnaukhov
 */
public class CodegenModelAdapter extends CodegenModel implements Item {

    private final CodegenModel codegenModel;

    public CodegenModelAdapter(CodegenModel codegenModel) {
        this.codegenModel = codegenModel;
    }

    @Override
    public String getType() {
        return codegenModel.getDataType();
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
