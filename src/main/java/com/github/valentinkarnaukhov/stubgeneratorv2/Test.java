package com.github.valentinkarnaukhov.stubgeneratorv2;

import com.github.valentinkarnaukhov.stubgenerator.model.Field;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.Item;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.Node;
import com.github.valentinkarnaukhov.stubgeneratorv2.model.adapter.CodegenModelAdapter;
import com.github.valentinkarnaukhov.stubgeneratorv2.parser.ModelParser;
import com.github.valentinkarnaukhov.stubgeneratorv2.parser.SpecParser;
import com.github.valentinkarnaukhov.stubgeneratorv2.visitor.NodeVisitor;
import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.CodegenConfig;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.util.InlineModelResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Valentin Karnaukhov
 */
public class Test {

    private static OpenAPI openAPI;
    private static CodegenConfig config;
    private static ClientOptInput clientOptInput;
    private static Map<String, CodegenModel> allModels;

    public static void main(String[] args) {
        CodegenConfigurator codegenConfigurator = new CodegenConfigurator();
        codegenConfigurator.setLang("java");
        codegenConfigurator.setInputSpec("parameter_adapter.yaml");

        openAPI = new OpenAPIV3Parser().read(codegenConfigurator.getInputSpec());
        InlineModelResolver resolver = new InlineModelResolver(true, true);
        resolver.flatten(openAPI);

        clientOptInput = codegenConfigurator.toClientOptInput();
        clientOptInput.setOpenAPI(openAPI);
        config = clientOptInput.getConfig();
        allModels = new SpecParser(clientOptInput, null).extractModels();

        Map<String, Item> models = new HashMap<>();
        for (Map.Entry<String, CodegenModel> entry : allModels.entrySet()) {
            models.put(entry.getKey(), new CodegenModelAdapter(entry.getValue()));
        }

        ModelParser parser = new ModelParser(3, allModels);
        Item source = models.get("Level1");
        Node node = parser.parse(source);
        NodeVisitor visitor = new NodeVisitor(null);
        visitor.visit(node);
        System.out.println("");
    }
}
