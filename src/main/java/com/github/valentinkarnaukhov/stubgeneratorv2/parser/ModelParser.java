package com.github.valentinkarnaukhov.stubgeneratorv2.parser;

import com.github.valentinkarnaukhov.stubgeneratorv2.model.Item;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.ModelNode;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.Node;

import java.util.ArrayList;
import java.util.List;
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
        } else {
            model = this.allModels.get(model.getType());
        }
        List<Node> children = new ArrayList<>();
        for (Item field : model.getFields()) {
            Node child = parse(this.allModels.get(field.getType()), currentDepth + 1);
            children.add(child);
        }
        return new ModelNode(model, children);
    }
}
