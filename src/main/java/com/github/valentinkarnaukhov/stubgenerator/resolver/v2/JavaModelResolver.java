package com.github.valentinkarnaukhov.stubgenerator.resolver.v2;

import com.github.valentinkarnaukhov.stubgenerator.model.ObjectTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;
import com.github.valentinkarnaukhov.stubgenerator.resolver.ResolverConf;

import java.util.Map;

import static io.swagger.codegen.v3.generators.DefaultCodegenConfig.camelize;

/**
 * @author Valentin Karnaukhov
 */
public class JavaModelResolver extends AbstractModelResolver implements ModelResolver {

    private final Map<String, CodegenParameter> allModels;
    private final ResolverConf conf;

    public JavaModelResolver(Map<String, CodegenParameter> allModels, ResolverConf conf) {
        this.allModels = allModels;
        this.conf = conf;
    }

    @Override
    public ObjectTemplate toObjectTemplate(CodegenParameter parameter) {
        ObjectTemplate objectTemplate = new ObjectTemplate();
        objectTemplate.setName(parameter.getName());
        objectTemplate.setCamelizedName(camelize(parameter.getName()));
        objectTemplate.setPrefix(this.conf.getCompNamePrefix());
        if (!this.allModels.containsKey(parameter.getDataType()) && !parameter.isCollection()) {
            //primitive
            objectTemplate.setPrimitive(true);
            objectTemplate.setCollection(false);
            objectTemplate.setBaseType(parameter.getDataType());
            objectTemplate.setType(parameter.getDataType());
        } else {
            if (parameter.isCollection()) {
                if (!this.allModels.containsKey(parameter.getBaseType())) {
                    //primitive list
                    objectTemplate.setPrimitive(true);
                    objectTemplate.setCollection(true);
                    objectTemplate.setBaseType(parameter.getDataType());
                    objectTemplate.setType(parameter.getDataType());
                } else {
                    //obj list
                    objectTemplate.setPrimitive(false);
                    objectTemplate.setCollection(true);
                    objectTemplate.setFields(modelToFields(this.allModels.get(parameter.getBaseType())));
                    objectTemplate.setBaseType(parameter.getDataType());
                    objectTemplate.setType(parameter.getDataType());
                }
                objectTemplate.setValue(parameter.getValue());
            } else {
                //obj
                objectTemplate.setPrimitive(false);
                objectTemplate.setCollection(false);
                objectTemplate.setFields(modelToFields(this.allModels.get(parameter.getBaseType())));
                objectTemplate.setBaseType(parameter.getBaseType());
                objectTemplate.setType(parameter.getBaseType());
            }
        }
        return objectTemplate;
    }

}
