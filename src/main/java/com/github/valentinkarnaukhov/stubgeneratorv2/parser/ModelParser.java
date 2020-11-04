package com.github.valentinkarnaukhov.stubgeneratorv2.parser;

import com.github.valentinkarnaukhov.stubgeneratorv2.model.Item;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.ModelNode;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Valentin Karnaukhov
 */
public class ModelParser {

    private final int maxDepth;
    private final Map<String, Item> allModels;

    public ModelParser(int maxDepth, Map<String, Item> allModels) {
        this.maxDepth = maxDepth;
        this.allModels = allModels;
    }

    public Node parse(Item item) {
        return parse(item, 0);
    }

    private Node parse(Item model, int currentDepth) {
        if (model.isPrimitive() || currentDepth >= maxDepth) {
            return new ModelNode(model);
        }
        Map<String, Node> children = new HashMap<>();
        for (Item field : model.getFields()) {
            if (!field.isPrimitive()) {
                children.put(field.getName(), parse(this.allModels.get(field.getType()), currentDepth + 1));
            } else {
                children.put(field.getName(), parse(field, currentDepth + 1));
            }
        }
        return new ModelNode(model, children);
    }
}
