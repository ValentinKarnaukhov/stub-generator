package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.model.CodegenConfiguration;

public class GeneratorExecutor {

    public void generate(CodegenConfiguration configuration) {
        WiremockGenerator wiremockGenerator = new WiremockGenerator();

        wiremockGenerator.configure(configuration);
        wiremockGenerator.generate();
    }

}
