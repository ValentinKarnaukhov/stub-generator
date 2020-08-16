package com.github.valentinkarnaukhov.stubgenerator.resolver;

import com.github.valentinkarnaukhov.stubgenerator.model.FieldTemplate;
import io.swagger.codegen.v3.CodegenProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResolverConfFactory {

    public static ResolverConf getForQuery() {
        ResolverConf conf = ResolverConf.builder()
                .compNamePrefix("inQuery")
                .compNameDelimiter("_")
                .compNameSuffix("")
                .build();
        Function<ModelResolver.Node, String> compositeNameFunction =
                n -> Stream.concat(n.getWay().stream(), Stream.of(n.getSourceProperty()))
                        .map(CodegenProperty::getNameInCamelCase)
                        .collect(Collectors.joining(conf.getCompNameDelimiter(), conf.getCompNamePrefix(), conf.getCompNameSuffix()));
        conf.setCompositeNameFunction(compositeNameFunction);
        return conf;
    }

    public static ResolverConf getForResponse() {
        ResolverConf conf = ResolverConf.builder()
                .wayToObjPrefix(".")
                .wayToObjDelimiter("().")
                .wayToObjSuffix("()")
                .build();

        BiFunction<CodegenProperty, ModelResolver.Node, FieldTemplate> propToField = (property, node) -> {
            FieldTemplate fieldTemplate = FieldTemplate.builder()
                    .fieldName(property.getNameInCamelCase())
                    .methodFieldName(property.getName())
                    .fieldType(property.getDatatype())
                    .baseType(property.getBaseType())
                    .setterName(property.getSetter())
//                    .wayToObject(node.getWayToObject().stream()
//                            .map(CodegenProperty::getGetter)
//                            .collect(Collectors.joining(
//                                    conf.getWayToObjDelimiter(),
//                                    conf.getWayToObjPrefix(),
//                                    conf.getWayToObjSuffix())))
                    .build();
//            Stream<String> way = Stream.concat(node.getWayToObject().stream().map(CodegenProperty::getNameInCamelCase),
//                    Stream.of(property.getNameInCamelCase()));
//            fieldTemplate.setCompositeFieldName(way.collect(Collectors.joining(
//                    conf.getFieldNameDelimiter(),
//                    conf.getFieldNamePrefix(),
//                    conf.getFieldNameSuffix())));
            if (fieldTemplate.getWayToObject().equals(conf.getWayToObjPrefix() + conf.getWayToObjSuffix())) {
                fieldTemplate.setWayToObject("");
            }
            return fieldTemplate;
        };
        conf.setPropertyToFields(propToField);
        return conf;
    }

    public static ResolverConf getForBody() {
        ResolverConf conf = ResolverConf.builder()
                .compNamePrefix("inBody")
                .compNameDelimiter("_")
                .compNameSuffix("")
                .build();
        Function<ModelResolver.Node, String> compositeNameFunction =
                n -> Stream.concat(n.getWay().stream(), Stream.of(n.getSourceProperty()))
                        .map(CodegenProperty::getNameInCamelCase)
                        .collect(Collectors.joining(conf.getCompNameDelimiter(), conf.getCompNamePrefix(), conf.getCompNameSuffix()));
        conf.setCompositeNameFunction(compositeNameFunction);
        return conf;
    }
}
