package com.github.valentinkarnaukhov.stubgenerator.resolver.v2;

import com.github.valentinkarnaukhov.stubgenerator.model.Field;
import com.github.valentinkarnaukhov.stubgenerator.model.ObjectTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;

import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public interface ModelResolver {

    ObjectTemplate toObjectTemplate(CodegenParameter parameter);
    List<Field> extractFields(CodegenParameter parameter);
    void getCollections();

}
