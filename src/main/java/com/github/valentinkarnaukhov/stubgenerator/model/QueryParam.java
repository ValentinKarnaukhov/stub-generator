package com.github.valentinkarnaukhov.stubgenerator.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QueryParam {

    private String queryParamName;
    private String queryParamType;
    private String methodQueryParamName;

}
