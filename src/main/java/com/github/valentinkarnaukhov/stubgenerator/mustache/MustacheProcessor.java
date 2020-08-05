package com.github.valentinkarnaukhov.stubgenerator.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;


public class MustacheProcessor {
    private static String outputPath =
            "./target/generated-sources/wiremockgenerator/src/main/java/com/github/valentinkarnaukhov/stubs/";

    public static void process(TagTemplate tagTemplate) throws IOException {
        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile("TagTemplate.mustache");
        File file = new File(outputPath+tagTemplate.getTag()+".java");
        Files.createDirectories(java.nio.file.Paths.get(outputPath));
        FileWriter writer = new FileWriter(file);
        mustache.execute(writer, tagTemplate);

        writer.close();
    }

}
