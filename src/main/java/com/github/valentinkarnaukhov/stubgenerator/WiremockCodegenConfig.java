package com.github.valentinkarnaukhov.stubgenerator;

import io.swagger.codegen.v3.CodegenType;
import io.swagger.codegen.v3.generators.DefaultCodegenConfig;
import io.swagger.codegen.v3.generators.java.AbstractJavaCodegen;

public class WiremockCodegenConfig extends AbstractJavaCodegen {
    @Override
    public String escapeUnsafeCharacters(String input) {
        return null;
    }

    @Override
    public String getDefaultTemplateDir() {
        return null;
    }

    @Override
    public CodegenType getTag() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public boolean checkAliasModel() {
        return false;
    }
}
