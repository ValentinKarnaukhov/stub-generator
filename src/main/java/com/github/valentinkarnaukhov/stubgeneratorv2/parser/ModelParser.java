package com.github.valentinkarnaukhov.stubgeneratorv2.parser;

import com.github.valentinkarnaukhov.stubgeneratorv2.model.Item;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.ModelNode;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.Node;
import io.swagger.codegen.v3.CodegenModel;

import java.util.Map;

/**
 * @author Valentin Karnaukhov
 */
public class ModelParser {

    private final int maxDepth;
    private final Map<String, CodegenModel> allModels;

    public ModelParser(int maxDepth, Map<String, CodegenModel> allModels) {
        this.maxDepth = maxDepth;
        this.allModels = allModels;
    }

    public Node parse(Item item) {
        Node head = new ModelNode();
        if (item.isPrimitive()) {
            return head;
        }
        return null;
    }
}
