package com.github.valentinkarnaukhov.stubgenerator.resolver.v2;

import com.github.valentinkarnaukhov.stubgenerator.model.ObjectTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;

/**
 * @author Valentin Karnaukhov
 */
public interface ModelResolver {

    ObjectTemplate toObjectTemplate(CodegenParameter parameter);

}
