package com.github.valentinkarnaukhov.stubgenerator.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import io.swagger.codegen.v3.generators.DefaultCodegenConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;


public class MustacheProcessor {
    private DefaultCodegenConfig codegenConfig;

    public MustacheProcessor(DefaultCodegenConfig codegenConfig){
        this.codegenConfig = codegenConfig;
    }

    public void process(TagTemplate tagTemplate) throws IOException {
        tagTemplate.setModelPackage(codegenConfig.modelPackage());

        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile("TagTemplate.mustache");

        File file = new File(codegenConfig.outputFolder()+tagTemplate.getTag()+".java");
        Files.createDirectories(java.nio.file.Paths.get(codegenConfig.outputFolder()));
        FileWriter writer = new FileWriter(file);

        mustache.execute(writer, tagTemplate);

        writer.close();
    }

}
