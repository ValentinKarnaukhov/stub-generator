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
        dfs(node.getChildren().values(), new Field());
        return fields;
    }

    public void dfs(Collection<Node> children, Field field) {
        for (Node child : children) {
            Field newField = SerializationUtils.clone(field);
            newField.setSetter(newField.getSetter() + child.getSetter());
            newField.setGetter(newField.getSetter() + child.getGetter());
            newField.setName(newField.getName() + child.getName());
            if (child.isPrimitive()) {
                fields.add(newField);
            } else {
                if(child.getChildren()!=null){
                    dfs(child.getChildren().values(), newField);
                }
            }
        }
    }

}
