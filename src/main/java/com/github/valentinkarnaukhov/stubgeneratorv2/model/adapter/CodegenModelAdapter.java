package com.github.valentinkarnaukhov.stubgeneratorv2.model.adapter;

import com.github.valentinkarnaukhov.stubgeneratorv2.model.Item;
import io.swagger.codegen.v3.CodegenModel;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Valentin Karnaukhov
 */
public class CodegenModelAdapter extends CodegenModel implements Item {

    private final CodegenModel codegenModel;
    private final Item fieldInformation;

    public CodegenModelAdapter(CodegenModel codegenModel) {
        this.codegenModel = codegenModel;
        this.fieldInformation = null;
    }

    public CodegenModelAdapter(CodegenModel codegenModel, Item fieldInformation) {
        this.codegenModel = codegenModel;
        this.fieldInformation = fieldInformation;
    }

    @Override
    public String getClassName() {
        return codegenModel.getClassname();
    }

    @Override
    public String getName() {
        return fieldInformation.getName();
    }

    @Override
    public String getSetter() {
        return fieldInformation.getSetter();
    }

    @Override
    public String getGetter() {
        return fieldInformation.getGetter();
    }

    @Override
    public boolean isPrimitive() {
        return codegenModel.getIsPrimitiveType();
    }

    @Override
    public boolean isCollection() {
        return fieldInformation.isCollection();
    }

    @Override
    public String getCollectionType() {
        return fieldInformation.getCollectionType();
    }

    @Override
    public List<Item> getFields() {
        return codegenModel.getAllVars().stream().map(CodegenPropertyAdapter::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return codegenModel.toString();
    }
}
