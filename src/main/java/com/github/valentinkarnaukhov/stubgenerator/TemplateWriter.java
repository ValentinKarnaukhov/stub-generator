package com.github.valentinkarnaukhov.stubgenerator;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class TemplateWriter {

    public String processTemplate(String templateFile, Object data) {
        try {
            TemplateLoader templateLoader;
            templateFile = templateFile.replace(".mustache", StringUtils.EMPTY).replace("\\", "/");
            templateLoader = new ClassPathTemplateLoader("/", ".mustache");
            final Handlebars handlebars = new Handlebars(templateLoader);
            return handlebars.compile(templateFile).apply(data);
        } catch (IOException e) {
            throw new RuntimeException("Template processing error");
        }
    }

}
