package com.github.valentinkarnaukhov.stubgenerator.util;

import com.github.valentinkarnaukhov.stubgenerator.model.FieldTemplate;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelResolver {

    private final Map<String, CodegenModel> allModels;
    private final int maxDepth;

    public ModelResolver(Map<String, CodegenModel> allModels, int maxDepth) {
        this.allModels = allModels;
        this.maxDepth = maxDepth;
    }

    public Node resolveFlatten(CodegenModel codegenModel, int depth) {
        return getNode(codegenModel, null, null, depth);
    }

    public List<FieldTemplate> toFields(Node node){
        List<FieldTemplate> fieldTemplates;
        return null;
    }


    private Node getNode(CodegenModel codegenModel, Node parent, CodegenProperty source, int depth) {
        Node node = new Node();
        node.setParameters(getProperties(codegenModel.getAllVars()));
        node.setDepth(depth);

        if (parent != null) {
            node.getWayToObject().addAll(parent.getWayToObject());
        }
        if (source != null) {
            node.getWayToObject().add(source);
        }

        int newDepth = depth + 1;

        if (newDepth == maxDepth) {
            node.setModels(null);
        } else {
            List<Node> nodes = codegenModel.getAllVars().stream()
                    .filter(prop -> prop.complexType != null)
                    .map(prop -> getNode(
                            this.allModels.get(prop.complexType),
                            node,
                            prop,
                            newDepth))
                    .collect(Collectors.toList());
            node.setModels(nodes);
        }

        return node;
    }

    @Getter
    @Setter
    public static class Node {
        private List<Node> models;
        private List<CodegenProperty> parameters;
        private List<CodegenProperty> wayToObject = new ArrayList<>();
        private int depth;
        private boolean visit;
    }

    private List<CodegenProperty> getProperties(List<CodegenProperty> properties) {
        return properties.stream()
                .filter(v -> v.complexType == null)
                .collect(Collectors.toList());
    }

}
