package com.github.valentinkarnaukhov.stubgenerator.resolver.v2;

import com.github.valentinkarnaukhov.stubgenerator.model.Field;
import com.github.valentinkarnaukhov.stubgenerator.model.ObjectTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;

import java.util.List;
import java.util.Map;

import static io.swagger.codegen.v3.generators.DefaultCodegenConfig.camelize;

/**
 * @author Valentin Karnaukhov
 */
public class JavaModelResolver extends AbstractModelResolver implements ModelResolver {

    private Map<String, CodegenParameter> allModels;

    public JavaModelResolver(Map<String, CodegenParameter> allModels) {
        this.allModels = allModels;
    }

    @Override
    public ObjectTemplate toObjectTemplate(CodegenParameter parameter) {
        ObjectTemplate objectTemplate = new ObjectTemplate();
        objectTemplate.setName(parameter.getName());
        objectTemplate.setCamelizedName(camelize(parameter.getName()));
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
                    objectTemplate.setFields(extractFields(this.allModels.get(parameter.getBaseType())));
                    objectTemplate.setBaseType(parameter.getBaseType());
                    objectTemplate.setType(parameter.getDataType());
                }
                objectTemplate.setValue(parameter.getValue());
            } else {
                //obj
                objectTemplate.setPrimitive(false);
                objectTemplate.setCollection(false);
                objectTemplate.setFields(extractFields(this.allModels.get(parameter.getBaseType())));
                objectTemplate.setBaseType(parameter.getBaseType());
                objectTemplate.setType(parameter.getBaseType());
            }
        }

        return objectTemplate;
    }

    @Override
    public List<Field> extractFields(CodegenParameter parameter) {
        return null;
    }

    @Override
    public void getCollections() {

    }
}
