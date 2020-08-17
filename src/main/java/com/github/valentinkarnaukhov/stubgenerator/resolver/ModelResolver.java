package com.github.valentinkarnaukhov.stubgenerator.resolver;

import com.github.valentinkarnaukhov.stubgenerator.model.Field;
import com.github.valentinkarnaukhov.stubgenerator.model.FieldTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.ObjectTemplate;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenParameter;
import io.swagger.codegen.v3.CodegenProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.swagger.codegen.v3.generators.DefaultCodegenConfig.camelize;

public class ModelResolver {

    private final Map<String, CodegenModel> allModels;
    private List<FieldTemplate> fieldTemplates;
    private ResolverConf conf;
    private int maxDepth = 6;
    @Getter
    private final Set<ObjectTemplate> collections = new HashSet<>();

    public ModelResolver(Map<String, CodegenModel> allModels, ResolverConf conf) {
        this.allModels = allModels;
        this.conf = conf;
    }

    public List<FieldTemplate> resolveFlatten(CodegenModel codegenModel, ResolverConf conf) {
        Node node = modelToNode(codegenModel, null, null, 0);
        Field field = nodeToField(node);
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
                        .baseType(parameter.getBaseType())
                        .build();
                fields.add(param);
            } else {
                CodegenModel model = this.allModels.get(parameter.baseType);
                List<FieldTemplate> fieldTemplates = resolveFlatten(model, conf);
                fields.addAll(fieldTemplates);
            }
        }
        return fields;
    }

    private List<FieldTemplate> toFields(Node node, ResolverConf conf) {
        this.fieldTemplates = new ArrayList<>();
        nodeToField(node);
        return fieldTemplates;
    }

    private Field nodeToField(Node node) {
        Field field = new Field();
//        field.getFields().addAll(node.getProperties().stream().map(this::propertyToField).collect(Collectors.toList()));
//        field.getFields().addAll(node.getModels().stream().map(this::nodeToField).collect(Collectors.toList()));
        return null;
    }

    private ObjectTemplate parameterToObject(CodegenParameter parameter) {
        ObjectTemplate objectTemplate = new ObjectTemplate();
        objectTemplate.setName(parameter.getParamName());
        objectTemplate.setCamelizedName(camelize(parameter.getParamName()));
        objectTemplate.setPrefix(this.conf.getCompNamePrefix());
        if (!this.allModels.containsKey(parameter.getDataType()) && !parameter.getIsListContainer()) {
            //primitive
            objectTemplate.setPrimitive(true);
            objectTemplate.setCollection(false);
            objectTemplate.setType(parameter.getDataType());
        } else {
            if (parameter.getIsListContainer()) {
                if (!this.allModels.containsKey(parameter.getBaseType())) {
                    //primitive list
                    objectTemplate.setPrimitive(true);
                    objectTemplate.setCollection(true);
                    objectTemplate.setType(parameter.getItems().getBaseType());
                } else {
                    //obj list
                    objectTemplate.setPrimitive(false);
                    objectTemplate.setCollection(true);
                    objectTemplate.setFields(modelToFields(this.allModels.get(parameter.getBaseType())));
                    objectTemplate.setType(parameter.getItems().getBaseType());
                }
            } else {
                //obj
                objectTemplate.setPrimitive(false);
                objectTemplate.setCollection(false);
                objectTemplate.setFields(modelToFields(this.allModels.get(parameter.getBaseType())));
                objectTemplate.setType(parameter.getBaseType());
            }
        }

        return objectTemplate;
    }

    private List<Field> modelToFields(CodegenModel model) {
        Node node = modelToNode(model, null, null, 0);
        List<Field> fields = new ArrayList<>();
        parseNode(node, fields);
        Consumer<ObjectTemplate> afterToObj = obj -> {
            if (this.allModels.containsKey(obj.getType())) {
                obj.setFields(modelToFields(this.allModels.get(obj.getType())));
            }
        };
        this.collections.addAll(fields.stream()
                .filter(Field::isCollection)
                .map(Field::toObjectTemplate)
                .peek(afterToObj)
                .collect(Collectors.toList()));
        return fields;
    }

    private void parseNode(Node node, List<Field> fields) {
        for (String key : node.getParameters().keySet()) {
            Node parameter = node.getParameters().get(key);
            Field field = new Field();
            field.setName(key);
            field.setCompositeName(this.conf.getCompositeNameFunction().apply(parameter));
            field.setWayToParent(this.conf.getWayToParentFunction().apply(parameter));

            if (parameter.getSourceModel() != null) {
                field.setPrimitive(false);
                field.setType(parameter.getSourceModel().getDataType());
            }
            if (parameter.getSourceProperty() != null) {
                if (parameter.getSourceProperty().getIsListContainer()) {
                    field.setPrimitive(parameter.getSourceProperty().getItems().getIsPrimitiveType());
                    field.setType(parameter.getSourceProperty().getItems().getBaseType());
                } else {
                    field.setPrimitive(parameter.getSourceProperty().getIsPrimitiveType());
                    field.setType(parameter.getSourceProperty().getBaseType());
                }
                field.setCollection(parameter.getSourceProperty().getIsListContainer());
                field.setSetter(parameter.getSourceProperty().getSetter());
                field.setGetter(parameter.getSourceProperty().getGetter());
            }
            fields.add(field);
        }
        node.getModels().values().forEach(v -> parseNode(v, fields));
    }

    public List<ObjectTemplate> parametersToObject(List<CodegenParameter> parameters) {
        return parameters.stream().map(this::parameterToObject).collect(Collectors.toList());
    }

    private Node modelToNode(CodegenModel sourceModel, CodegenProperty sourceProperty, Node parentNode, int depth) {
        Node node = new Node();
        node.setDepth(depth);
        node.setSourceModel(sourceModel);
        node.setSourceProperty(sourceProperty);
        if (parentNode != null) {
            node.getWay().addAll(parentNode.getWay());
            if (parentNode.getSourceProperty() != null) {
                node.getWay().add(parentNode.getSourceProperty());
            }
        }

        if (sourceModel != null) {
            Map<String, Node> parameters = sourceModel.getAllVars().stream()
                    .filter(p -> p.getComplexType() == null || p.getIsListContainer() || depth == maxDepth)
                    .collect(Collectors.groupingBy(CodegenProperty::getBaseName, Collectors.mapping(
                            p -> propertyToNode(p, sourceProperty, node, depth), Collectors.reducing(null, (a, b) -> b))));
            node.setParameters(parameters);
            Map<String, Node> models = sourceModel.getAllVars().stream()
                    .filter(p -> p.getComplexType() != null && depth < maxDepth)
                    .collect(Collectors.groupingBy(CodegenProperty::getBaseName, Collectors.mapping(
                            p -> propertyToNode(p, sourceProperty, node, depth), Collectors.reducing(null, (a, b) -> b))));
            node.setModels(models);
        }

        return node;
    }

    private Node propertyToNode(CodegenProperty sourceProperty, CodegenProperty parentProperty, Node parentNode, int depth) {
        Node node = new Node();

        if (depth + 1 <= maxDepth) {
            node = modelToNode(this.allModels.get(sourceProperty.getBaseType()), sourceProperty, parentNode, depth + 1);
        } else {
            node.setDepth(depth + 1);
            node.setSourceProperty(sourceProperty);
            node.getWay().addAll(parentNode.getWay());
            node.getWay().add(parentProperty);
        }

        return node;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Node {
        private CodegenModel sourceModel;
        private CodegenProperty sourceProperty;
        private Map<String, Node> parameters = new HashMap<>();
        private Map<String, Node> models = new HashMap<>();
        private List<CodegenProperty> way = new ArrayList<>();
        private int depth;
        private boolean visit;
    }

}
