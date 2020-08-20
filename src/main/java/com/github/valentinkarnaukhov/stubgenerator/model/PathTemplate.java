package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PathTemplate {

    private String operationId;
    private String path;
    private String httpMethod;
    private List<ResponseTemplate> responses;
    private List<ObjectTemplate> queryParams;
    private List<ObjectTemplate> bodyParams;
    private List<ObjectTemplate> params; // contains bodyParams and responses
    private Set<ObjectTemplate> collections = new HashSet<>();
}
