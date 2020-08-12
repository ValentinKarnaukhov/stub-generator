package com.github.valentinkarnaukhov.stubgenerator.resolver;

import com.github.valentinkarnaukhov.stubgenerator.model.FieldTemplate;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenParameter;
import io.swagger.codegen.v3.CodegenProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.swagger.codegen.v3.generators.DefaultCodegenConfig.camelize;

public class ModelResolver {

    private final Map<String, CodegenModel> allModels;
    private List<FieldTemplate> fieldTemplates;

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
            if (parameter.getIsPrimitiveType()) {
                FieldTemplate param = FieldTemplate.builder()
                        .fieldName(camelize(parameter.getParamName()))
                        .methodFieldName(parameter.getParamName())
                        .fieldType(parameter.getDataType())
                        .build();
                fields.add(param);
            } else {
                CodegenModel model = this.allModels.get(parameter.baseType);
                fields.addAll(resolveFlatten(model, maxDepth, conf));
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
        for (CodegenProperty parameter : node.getParameters()) {
            FieldTemplate fieldTemplate = FieldTemplate.builder()
                    .fieldName(parameter.getNameInCamelCase())
                    .methodFieldName(parameter.getName())
                    .fieldType(parameter.getDatatypeWithEnum())
                    .setterName(parameter.getSetter())
                    .wayToObject(node.getWayToObject().stream()
                            .map(CodegenProperty::getGetter)
                            .collect(Collectors.joining(
                                    conf.getWayToObjDelimiter(),
                                    conf.getWayToObjPrefix(),
                                    conf.getWayToObjSuffix())))
                    .build();
            Stream<String> way = Stream.concat(node.getWayToObject().stream().map(CodegenProperty::getNameInCamelCase),
                    Stream.of(parameter.getNameInCamelCase()));
            fieldTemplate.setCompositeFieldName(way.collect(Collectors.joining(
                    conf.getFieldNameDelimiter(),
                    conf.getFieldNamePrefix(),
                    conf.getFieldNameSuffix())));
            fieldTemplate.setWayToObject(fieldTemplate.getWayToObject().equals(".()") ? "" : fieldTemplate.getWayToObject());
            fieldTemplates.add(fieldTemplate);
        }
        if (node.getModels() != null) {
            node.getModels().forEach(v -> nodeToField(v, conf));
        }
    }

    private Node getNode(CodegenModel codegenModel, Node parent, CodegenProperty source, int depth, int maxDepth) {
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

        if (depth == maxDepth) {
            node.setModels(null);
            node.setParameters(codegenModel.getAllVars());
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
        private List<CodegenProperty> parameters;
        private List<CodegenProperty> wayToObject = new ArrayList<>();
        private int depth;
        private boolean visit;

        public Node(List<CodegenProperty> parameters, int depth) {
            this.parameters = parameters;
            this.depth = depth;
        }
    }

    private List<CodegenProperty> getProperties(List<CodegenProperty> properties) {
        return properties.stream()
                .filter(v -> v.complexType == null)
                .collect(Collectors.toList());
    }

}