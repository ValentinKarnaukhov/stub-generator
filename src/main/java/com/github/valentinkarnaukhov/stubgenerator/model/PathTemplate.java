package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PathTemplate {

    private String operationId;
    private String path;
    private String httpMethod;
    private List<ResponseTemplate> responses;
    private List<FieldTemplate> queryParams;
    private List<FieldTemplate> bodyParams;
}
