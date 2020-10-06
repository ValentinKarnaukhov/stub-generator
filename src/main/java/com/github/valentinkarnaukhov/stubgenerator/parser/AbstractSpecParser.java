package com.github.valentinkarnaukhov.stubgenerator.parser;

import com.github.valentinkarnaukhov.stubgenerator.model.ObjectTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.PathTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.ResponseTemplate;
import com.github.valentinkarnaukhov.stubgenerator.model.adapter.CodegenParameter;
import com.github.valentinkarnaukhov.stubgenerator.resolver.v2.ModelResolver;
import io.swagger.codegen.v3.CodegenModel;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.codegen.v3.CodegenResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Valentin Karnaukhov
 */
public abstract class AbstractSpecParser {

    protected ModelResolver modelResolver;

    public void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }

    public PathTemplate operationToPath(CodegenOperation oper){
        PathTemplate pathTemplate = new PathTemplate();

        pathTemplate.setOperationId(StringUtils.capitalize(oper.getOperationId()));
        pathTemplate.setPath(oper.getPath());
        pathTemplate.setHttpMethod(oper.getHttpMethod().toLowerCase());
        pathTemplate.setResponses(new ArrayList<>());

        List<ObjectTemplate> queryParams = processQueryParameters(oper.getQueryParams());
        pathTemplate.setQueryParams(queryParams);

        List<ObjectTemplate> bodyParams = processBodyParameters(oper.getBodyParams());
        pathTemplate.setBodyParams(bodyParams);

        List<ResponseTemplate> responsesParams = new ArrayList<>();
        for (CodegenResponse response : oper.getResponses()) {
            responsesParams.add(processResponse(response));
        }
        pathTemplate.setResponses(responsesParams);

        pathTemplate.getCollections().addAll(queryParamResolver.getCollections());
        pathTemplate.getCollections().addAll(bodyParamResolver.getCollections());
        pathTemplate.getCollections().addAll(responseResolver.getCollections());
        Supplier<Stream<ObjectTemplate>> paramsSup = () -> {
            Stream<ObjectTemplate> params = Stream.concat(queryParams.stream(), bodyParams.stream());
            params = Stream.concat(params, responsesParams.stream().filter(v -> v.getResponse() != null)
                    .map(ResponseTemplate::getResponse));
            return params;
        };
        pathTemplate.getCollections().addAll(paramsSup.get()
                .filter(ObjectTemplate::isCollection)
                .collect(Collectors.toList()));
        return pathTemplate;
    }


}
