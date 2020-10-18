package com.github.valentinkarnaukhov.stubgenerator.resolver.v2;

import com.github.valentinkarnaukhov.stubgenerator.model.Field;
import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Valentin Karnaukhov
 */
public class FieldExtractor {

    private final int maxDepth;
    private final Map<String, CodegenParameter> allModels;

    public FieldExtractor(int depth, Map<String, CodegenParameter> allModels) {
        this.maxDepth = depth;
        this.allModels = allModels;
    }

    public List<Field> extractFields(CodegenParameter parameter) {

    }

    private AbstractModelResolver.Node parameterToNode(CodegenParameter codegenParameter) {
        AbstractModelResolver.Node node = new AbstractModelResolver.Node();

        Map<String, CodegenParameter> primitiveFields = codegenParameter
                .getAllVars()
                .stream()
                .filter(CodegenParameter::isPrimitiveType)
                .collect(Collectors.toMap(CodegenParameter::getName, Function.identity()));
    }


}
