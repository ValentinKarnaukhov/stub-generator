package com.github.valentinkarnaukhov.stubgeneratorv2.parser;

import com.github.valentinkarnaukhov.stubgeneratorv2.model.Item;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.ModelNode;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.Node;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.adapter.CodegenModelAdapter;
import io.swagger.codegen.v3.CodegenModel;

import java.util.ArrayList;
import java.util.List;
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
        return parse(item, 0);
    }

    private Node parse(Item model, int currentDepth) {
        if (model.isPrimitive() || currentDepth >= maxDepth) {
            return new ModelNode(model);
        }
        List<Node> children = parseFields(model.getFields(), currentDepth);
        return new ModelNode(model, children);
    }

    private List<Node> parseFields(List<Item> fields, int currentDepth) {
        List<Node> nodes = new ArrayList<>();
        for (Item field : fields) {
            if (field.isPrimitive()) {
                nodes.add(new ModelNode(field));
            } else {
                Item extendedCodegenModel = new CodegenModelAdapter(allModels.get(field.getClassName()), field);
                Node node = parse(extendedCodegenModel, currentDepth + 1);
                nodes.add(node);
            }
        }
        return nodes;
    }
}
