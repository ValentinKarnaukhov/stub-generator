package com.github.valentinkarnaukhov.stubgeneratorv2.visitor;

import com.github.valentinkarnaukhov.stubgenerator.model.Field;
import com.github.valentinkarnaukhov.stubgenerator.resolver.ResolverConf;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.Node;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public class NodeVisitor {

    private final ResolverConf conf;
    private final List<Field> fields = new ArrayList<>();

    public NodeVisitor(ResolverConf conf) {
        this.conf = conf;
    }

    public void visit(Node node) {
        for (Node child : node.getChildren()) {
            Field parent = createNewField(child);
            if (child.isPrimitive() || child.getChildren().isEmpty()) {
                fields.add(parent);
            } else {
                for (Node grandson : child.getChildren()) {
                    visit(grandson, SerializationUtils.clone(parent));
                }
            }
        }
    }

    private void visit(Node node, Field parent) {
        Field field = extendField(parent, node);
        if (node.isPrimitive() || node.getChildren().isEmpty()) {
            fields.add(field);
        } else {
            for (Node child : node.getChildren()) {
                visit(child, SerializationUtils.clone(field));
            }
        }
    }

    private Field createNewField(Node node) {
        Field field = new Field();
        field.setPrimitive(node.isPrimitive());
        field.setCollection(node.isCollection());
        field.setType(node.getClassName());
        field.setName(node.getName());
        field.setSetter(node.getSetter());
        field.setGetter(node.getGetter());
        return field;
    }

    private Field extendField(Field field, Node node) {
        field.setPrimitive(node.isPrimitive());
        field.setCollection(node.isCollection());
        field.setType(node.getClassName());
        field.setName(node.getName());
        field.setSetter(field.getSetter() + node.getSetter());
        field.setGetter(field.getGetter() + node.getGetter());
        return field;
    }
}
