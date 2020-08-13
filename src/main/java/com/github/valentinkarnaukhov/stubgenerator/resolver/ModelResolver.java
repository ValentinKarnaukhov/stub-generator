package com.github.valentinkarnaukhov.stubgenerator.resolver;

import com.github.valentinkarnaukhov.stubgenerator.model.FieldTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.ObjectTemplate;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenParameter;
import io.swagger.codegen.v3.CodegenProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

import static io.swagger.codegen.v3.generators.DefaultCodegenConfig.camelize;

public class ModelResolver {

    private final Map<String, CodegenModel> allModels;
    private List<FieldTemplate> fieldTemplates;
    @Getter private final Set<ObjectTemplate> collections = new HashSet<>();

    public ModelResolver(Map<String, CodegenModel> allModels) {
        this.allModels = allModels;
    }

    public List<FieldTemplate> resolveFlatten(CodegenModel codegenModel, int maxDepth, ResolverConf conf) {
        Node node = getNode(codegenModel, null, null, 0, maxDepth);
        return toFields(node, conf);
    }

    public List<FieldTemplate> resolveParameter(List<CodegenParameter> parameters, int maxDepth, ResolverConf conf) {
        List<FieldTemplate> fields = new ArrayList<>();
        for (CodegenParameter parameter : parameters) {
            if (!this.allModels.containsKey(parameter.baseType)) {
                FieldTemplate param = FieldTemplate.builder()
                        .fieldName(camelize(parameter.getParamName()))
                        .methodFieldName(parameter.getParamName())
                        .fieldType(parameter.getDataType())
                        .build();
                fields.add(param);
            } else {
                CodegenModel model = this.allModels.get(parameter.baseType);
                List<FieldTemplate> fieldTemplates = resolveFlatten(model, maxDepth, conf);
                fields.addAll(fieldTemplates);
            }
        }
        return fields;
    }

    private List<FieldTemplate> toFields(Node node, ResolverConf conf) {
        this.fieldTemplates = new ArrayList<>();
        nodeToField(node, conf);
        return fieldTemplates;
    }

    private void nodeToField(Node node, ResolverConf conf) {
        for (CodegenProperty property : node.getProperties()) {
            FieldTemplate fieldTemplate = conf.getPropertyToFields().apply(property, node);
            fieldTemplates.add(fieldTemplate);
        }
        if (node.getModels() != null) {
            node.getModels().forEach(v -> nodeToField(v, conf));
        }
    }

    private Node getNode(CodegenModel codegenModel, Node parent, CodegenProperty source, int depth, int maxDepth) {
        Node node = new Node();
        node.setProperties(getProperties(codegenModel.getAllVars()));
        node.setDepth(depth);

        if (parent != null) {
            node.getWayToObject().addAll(parent.getWayToObject());
        }
        if (source != null) {
            node.getWayToObject().add(source);
        }

        int newDepth = depth + 1;

        if (depth == maxDepth) {
            node.setModels(null);
            node.setProperties(codegenModel.getAllVars());
        } else {
            List<Node> nodes = codegenModel.getAllVars().stream()
                    .filter(prop -> prop.complexType != null)
                    .map(prop -> getNode(
                            this.allModels.get(prop.complexType),
                            node,
                            prop,
                            newDepth,
                            maxDepth))
                    .collect(Collectors.toList());
            node.setModels(nodes);
        }

        return node;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Node {
        private List<Node> models;
        private List<CodegenProperty> properties;
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
