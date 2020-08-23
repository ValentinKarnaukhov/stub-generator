package com.github.valentinkarnaukhov.stubgenerator.resolver;

import com.github.valentinkarnaukhov.stubgenerator.model.Field;
import com.github.valentinkarnaukhov.stubgenerator.model.ObjectTemplate;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenParameter;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.codegen.v3.CodegenResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.swagger.codegen.v3.generators.DefaultCodegenConfig.camelize;

public class ModelResolver {

    private final Map<String, CodegenModel> allModels;
    private final ResolverConf conf;
    private final int maxDepth;

    @Getter
    private final Set<ObjectTemplate> collections = new HashSet<>();

    public ModelResolver(Map<String, CodegenModel> allModels, int maxDepth, ResolverConf conf) {
        this.allModels = allModels;
        this.maxDepth = maxDepth;
        this.conf = conf;
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
            objectTemplate.setBaseType(parameter.getDataType());
            objectTemplate.setType(parameter.getDataType());
        } else {
            if (parameter.getIsListContainer()) {
                if (!this.allModels.containsKey(parameter.getBaseType())) {
                    //primitive list
                    objectTemplate.setPrimitive(true);
                    objectTemplate.setCollection(true);
                    objectTemplate.setBaseType(parameter.getItems().getBaseType());
                    objectTemplate.setType(parameter.getDataType());
                } else {
                    //obj list
                    objectTemplate.setPrimitive(false);
                    objectTemplate.setCollection(true);
                    objectTemplate.setFields(modelToFields(this.allModels.get(parameter.getBaseType())));
                    objectTemplate.setBaseType(parameter.getItems().getBaseType());
                    objectTemplate.setType(parameter.getDataType());
                }
                objectTemplate.setValue(parameter.getItems().getDefaultValue());
            } else {
                //obj
                objectTemplate.setPrimitive(false);
                objectTemplate.setCollection(false);
                objectTemplate.setFields(modelToFields(this.allModels.get(parameter.getBaseType())));
                objectTemplate.setBaseType(parameter.getBaseType());
                objectTemplate.setType(parameter.getBaseType());
            }
        }

        return objectTemplate;
    }

    public ObjectTemplate responseToObject(CodegenResponse codegenResponse) {
        if (codegenResponse.getDataType() == null) {
            return null;
        }
        ObjectTemplate objectTemplate = new ObjectTemplate();
        objectTemplate.setPrefix(this.conf.getCompNamePrefix());
        if (!this.allModels.containsKey(codegenResponse.getBaseType()) && !codegenResponse.getIsListContainer()) {
            //primitive
            objectTemplate.setPrimitive(true);
            objectTemplate.setCollection(false);
            objectTemplate.setBaseType(codegenResponse.getDataType());
            objectTemplate.setType(codegenResponse.getDataType());
        } else {
            if (codegenResponse.getIsListContainer()) {
                if (!this.allModels.containsKey(codegenResponse.getBaseType())) {
                    //primitive list
                    objectTemplate.setPrimitive(true);
                    objectTemplate.setCollection(true);
                    objectTemplate.setBaseType(codegenResponse.getBaseType());
                    objectTemplate.setType(codegenResponse.getDataType());
                } else {
                    //obj list
                    objectTemplate.setPrimitive(false);
                    objectTemplate.setCollection(true);
                    objectTemplate.setFields(modelToFields(this.allModels.get(codegenResponse.getBaseType())));
                    objectTemplate.setBaseType(codegenResponse.getBaseType());
                    objectTemplate.setType(codegenResponse.getDataType());
                }
            } else {
                //obj
                objectTemplate.setPrimitive(false);
                objectTemplate.setCollection(false);
                objectTemplate.setFields(modelToFields(this.allModels.get(codegenResponse.getBaseType())));
                objectTemplate.setBaseType(codegenResponse.getBaseType());
                objectTemplate.setType(codegenResponse.getBaseType());
            }
        }
        return objectTemplate;
    }

    private List<Field> modelToFields(CodegenModel model) {
        Node node = modelToNode(model, null, null, 0);
        List<Field> fields = new ArrayList<>();
        parseNode(node, fields);
        //TODO add infinity recursion exit condition
        Consumer<ObjectTemplate> afterToObj = obj -> {
            if (this.allModels.containsKey(obj.getBaseType())) {
                obj.setFields(modelToFields(this.allModels.get(obj.getBaseType())));
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
            field.setParentSetter(this.conf.getParentSetterFunction().apply(parameter));
            field.setJsonPath(this.conf.getJsonPathFunction().apply(parameter));
            CodegenProperty lastInWay = parameter.getWay().isEmpty() ? null : parameter.getWay().get(parameter.getWay().size() - 1);
            field.setParentType(lastInWay != null ? lastInWay.getBaseType() : null);

            if (parameter.getSourceModel() != null) {
                field.setPrimitive(false);
                field.setBaseType(parameter.getSourceModel().getDataType());
            }
            if (parameter.getSourceProperty() != null) {
                if (parameter.getSourceProperty().getIsListContainer()) {
                    field.setPrimitive(parameter.getSourceProperty().getItems().getIsPrimitiveType());
                    field.setBaseType(parameter.getSourceProperty().getItems().getBaseType());
                } else {
                    field.setPrimitive(parameter.getSourceProperty().getIsPrimitiveType());
                    field.setBaseType(parameter.getSourceProperty().getBaseType());
                }
                field.setCollection(parameter.getSourceProperty().getIsListContainer());
                field.setSetter(parameter.getSourceProperty().getSetter());
                field.setGetter(parameter.getSourceProperty().getGetter());
                field.setType(parameter.getSourceProperty().getDatatype());
                field.setValue(parameter.getSourceProperty().getDefaultValue());
            }
            fields.add(field);
        }
        node.getModels().values().forEach(v -> parseNode(v, fields));
    }

    public List<ObjectTemplate> parametersToObjects(List<CodegenParameter> parameters) {
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
            if (depth + 1 < maxDepth) {
                Map<String, Node> parameters = sourceModel.getAllVars().stream()
                        .filter(p -> p.getComplexType() == null || p.getIsListContainer())
                        .collect(Collectors.groupingBy(CodegenProperty::getBaseName, Collectors.mapping(
                                p -> propertyToNode(p, sourceProperty, node, depth), Collectors.reducing(null, (a, b) -> b))));
                node.setParameters(parameters);
                Map<String, Node> models = sourceModel.getAllVars().stream()
                        .filter(p -> p.getComplexType() != null)
                        .collect(Collectors.groupingBy(CodegenProperty::getBaseName, Collectors.mapping(
                                p -> propertyToNode(p, sourceProperty, node, depth), Collectors.reducing(null, (a, b) -> b))));
                node.setModels(models);
            } else if (maxDepth != 0) {
                Map<String, Node> parameters = sourceModel.getAllVars().stream()
                        .collect(Collectors.groupingBy(CodegenProperty::getBaseName, Collectors.mapping(
                                p -> propertyToNode(p, sourceProperty, node, depth), Collectors.reducing(null, (a, b) -> b))));
                node.setParameters(parameters);
            }
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
            node.getWay().addAll(parentNode.getWay().stream().filter(Objects::nonNull).collect(Collectors.toList()));
            if (parentProperty != null) {
                node.getWay().add(parentProperty);
            }
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
