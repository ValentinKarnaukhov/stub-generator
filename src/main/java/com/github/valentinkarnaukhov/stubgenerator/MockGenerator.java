package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.model.CodegenConfiguration;
import com.github.valentinkarnaukhov.stubgenerator.model.GeneratorProperties;

public class MockGenerator {

    public static void main(String[] args) {
        GeneratorExecutor executor = new GeneratorExecutor();
        CodegenConfiguration configuration = new CodegenConfiguration();
        GeneratorProperties properties = configuration.getGeneratorProperties();

        configuration.setModelPackage("com.github.valentinkarnaukhov.stubgenerator.model");
        configuration.setLang("java");
        configuration.setInputSpec("src/test/resources/test_swagger_2_0.yaml");
        configuration.setOutputDir("target/generated-sources/swagger");
        configuration.setTemplateDir("src/main/resources");
        properties.setExplode(true);
        properties.setUseTags(true);
        properties.getPrefixMap().put("query", "inQuery");
        properties.getPrefixMap().put("body", "inBody");
        properties.getPrefixMap().put("response", "inResp");
        properties.setMaxDepth(1);

        executor.generate(configuration);
    }

}
