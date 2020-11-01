package com.github.valentinkarnaukhov.stubgeneratorv2.model;

import java.util.Map;

/**
 * @author Valentin Karnaukhov
 */
public class ModelNode implements Node {

    private EmbeddedAttribute value;
    private Map<String, Node> children;

    @Override
    public Map<String, Node> getChildren() {
        return children;
    }

    @Override
    public String getName() {
        return value.getName();
    }

    @Override
    public boolean isCollection() {
        return value.isCollection();
    }

    @Override
    public String getItemType() {
        return value.getItemType();
    }

    @Override
    public String getSetter() {
        return value.getSetter();
    }

    @Override
    public String getGetter() {
        return value.getGetter();
    }

    @Override
    public String getType() {
        return value.getType();
    }

    @Override
    public boolean isPrimitive() {
        return value.isPrimitive();
    }
}
