package com.github.valentinkarnaukhov.stubgeneratorv2.model;

import java.util.Collections;
import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public class ModelNode implements Node {

    private final Item source;
    private final List<Node> children;

    public ModelNode(Item source) {
        this.source = source;
        this.children = Collections.emptyList();
    }

    public ModelNode(Item source, List<Node> children) {
        this.source = source;
        this.children = children;
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }

    @Override
    public String getType() {
        return source.getType();
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getSetter() {
        return source.getSetter();
    }

    @Override
    public String getGetter() {
        return source.getGetter();
    }

    @Override
    public boolean isPrimitive() {
        return source.isPrimitive();
    }

    @Override
    public boolean isCollection() {
        return source.isCollection();
    }

    @Override
    public String getCollectionType() {
        return source.getCollectionType();
    }

    @Override
    public List<Item> getFields() {
        return source.getFields();
    }
}
