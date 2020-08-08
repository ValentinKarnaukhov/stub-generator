package com.github.valentinkarnaukhov.stubgenerator.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;
import io.swagger.codegen.v3.CodegenConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MustacheProcessor {
    private CodegenConfig codegenConfig;

    public MustacheProcessor(CodegenConfig codegenConfig){
        this.codegenConfig = codegenConfig;
    }

    public void process(TagTemplate tagTemplate) throws IOException {
        tagTemplate.setModelPackage(codegenConfig.modelPackage());

        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile("TagTemplate.mustache");

        File file = new File(codegenConfig.outputFolder()+"/"+tagTemplate.getTag()+".java");
        File parent = new File(file.getParent());
        parent.mkdirs();
        FileWriter writer = new FileWriter(file);

        mustache.execute(writer, tagTemplate);

        writer.close();
    }

}
