package com.github.valentinkarnaukhov.stubgenerator.resolver.v2;

import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Valentin Karnaukhov
 */
public abstract class AbstractModelResolver {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Node {
        private CodegenParameter sourceModel;
        private CodegenParameter sourceProperty;
        private Map<String, Node> primitiveFields = new HashMap<>();
        private Map<String, Node> referenceFields = new HashMap<>();
        private List<CodegenParameter> way = new ArrayList<>();
        private int depth;
        private boolean visit;
    }


}
