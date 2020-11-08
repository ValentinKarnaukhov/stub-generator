package com.github.valentinkarnaukhov.stubgeneratorv2.parser;

import com.github.valentinkarnaukhov.stubgenerator.model.Field;
import com.github.valentinkarnaukhov.stubgenerator.resolver.ResolverConf;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.Node;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public class FieldExtractor {

    private final ResolverConf conf;
    private final List<Field> fields = new ArrayList<>();

    public FieldExtractor(ResolverConf conf) {
        this.conf = conf;
    }

    public List<Field> extractFields(Node node) {
        deepFirstSearch(node.getChildren());
        return fields;
    }

    private void deepFirstSearch(Collection<Node> children) {
        for (Node node : children) {
            Field field = new Field();
            field.setName(node.getName());
            field.setGetter(node.getGetter());
            field.setSetter(node.getSetter());
            field.setType(node.getType());
            field.setPrimitive(node.isPrimitive());
            field.setCollection(node.isCollection());
            if (node.isPrimitive()) {
                fields.add(field);
            } else {
                deepFirstSearch(node.getChildren(), field);
            }
        }
    }

    private void deepFirstSearch(Collection<Node> children, Field parent) {
        for (Node child : children) {
            Field newField = SerializationUtils.clone(parent);
            newField.setSetter(newField.getSetter() + child.getSetter());
            newField.setGetter(newField.getSetter() + child.getGetter());
            newField.setName(newField.getName() + child.getName());
            if (child.isPrimitive()) {
                fields.add(newField);
            } else {
                deepFirstSearch(child.getChildren(), newField);
            }
        }
    }

}
