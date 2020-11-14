package com.github.valentinkarnaukhov.stubgeneratorv3;

import com.github.valentinkarnaukhov.stubgenerator.model.CodegenConfiguration;
import com.github.valentinkarnaukhov.stubgenerator.model.GeneratorProperties;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import com.github.valentinkarnaukhov.stubgeneratorv3.processor.SpecProcessor;
import com.github.valentinkarnaukhov.stubgeneratorv3.processor.TemplateProcessor;

import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public class WiremockStubGenerator {

    private CodegenConfiguration codegenConfiguration;
    private GeneratorProperties generatorProperties;
    private SpecProcessor specProcessor;
    private TemplateProcessor templateProcessor;

    public void generate() {
        List<TagTemplate> templates = specProcessor.process();
    }


}
