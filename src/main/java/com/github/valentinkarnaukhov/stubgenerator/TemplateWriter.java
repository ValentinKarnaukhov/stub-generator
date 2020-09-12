package com.github.valentinkarnaukhov.stubgenerator;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class TemplateWriter {

    public String processTemplate(String templateFile, Object data) {
        try {
            templateFile = templateFile.replace(".mustache", StringUtils.EMPTY).replace("\\", "/");
            final String templateDir = "";
            TemplateLoader templateLoader;
            templateFile = resolveTemplateFile(templateDir, templateFile);
            templateLoader = new ClassPathTemplateLoader("/" + templateDir, ".mustache");
            final Handlebars handlebars = new Handlebars(templateLoader);
            return handlebars.compile(templateFile).apply(data);
        } catch (IOException e) {
            throw new RuntimeException("Template processing error");
        }
    }

    private String resolveTemplateFile(String templateDir, String templateFile) {
        if (templateFile.startsWith(templateDir)) {
            templateFile = StringUtils.replaceOnce(templateFile, templateDir, StringUtils.EMPTY);
        }
        return templateFile;
    }

}
