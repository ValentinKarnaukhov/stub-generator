package com.github.valentinkarnaukhov.stubgenerator.generator;

import com.github.valentinkarnaukhov.stubgenerator.model.CodegenConfiguration;

import java.io.File;
import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public interface Generator extends io.swagger.codegen.v3.Generator {

    void configure(CodegenConfiguration configuration);
    void validate();

}
