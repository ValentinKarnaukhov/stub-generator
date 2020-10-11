package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.generator.Generator;
import com.github.valentinkarnaukhov.stubgenerator.generator.WiremockStubGenerator;
import com.github.valentinkarnaukhov.stubgenerator.model.CodegenConfiguration;

public class GeneratorExecutor {

    public void generate(CodegenConfiguration configuration) {
        Generator generator = new WiremockStubGenerator();

        generator.configure(configuration);
        generator.generate();
    }

}
