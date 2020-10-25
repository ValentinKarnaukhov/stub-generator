package com.github.valentinkarnaukhov.stubgenerator.model.adapter;

import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.codegen.v3.CodegenResponse;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class CodegenParameter {

    private final boolean isCollection;
    private final boolean isPrimitiveType;
    private final String name;
    private final String type;//List, String...
    private final String baseType;//String, Long, Object
    private final String setter;
    private final String getter;
    private final String value;
    private final List<CodegenParameter> allVars;

    public static CodegenParameter fromModel(CodegenModel model) {
        return CodegenParameter.builder()
                .isCollection(model.getIsListContainer())
                .isPrimitiveType(model.getIsPrimitiveType())
                .name(model.getName())
                .setter(null)
                .getter(null)
                .value(model.getDefaultValue())
                .allVars(model.getAllVars().stream().map(CodegenParameter::fromProperty).collect(Collectors.toList()))
                .build();
    }

    public static CodegenParameter fromProperty(CodegenProperty property) {
        return CodegenParameter.builder()
                .isCollection(property.getIsListContainer())
                .isPrimitiveType(property.getIsPrimitiveType())
                .name(property.getName())
                .setter(property.getSetter())
                .getter(property.getGetter())
                .value(property.getDefaultValue())
                .allVars(null)
                .build();
    }

    public static CodegenParameter fromParameter(io.swagger.codegen.v3.CodegenParameter parameter) {
        return CodegenParameter.builder()
                .isCollection(parameter.getIsListContainer())
                .isPrimitiveType(parameter.getIsPrimitiveType())
                .name(parameter.getParamName())
                .type(parameter.getDataType())
                .baseType(parameter.getIsListContainer() ? parameter.getBaseType() : parameter.getDataType())
                .setter(null)
                .getter(null)
                .value(parameter.getDefaultValue())
                .allVars(null)
                .build();
    }

    public static CodegenParameter fromResponse(CodegenResponse response) {
        return CodegenParameter.builder()
                .isCollection(response.getIsListContainer())
                .isPrimitiveType(response.getSimpleType())
                .type(response.getDataType())
                .baseType(StringUtils.capitalize(response.getBaseType()))
                .build();
    }
}
