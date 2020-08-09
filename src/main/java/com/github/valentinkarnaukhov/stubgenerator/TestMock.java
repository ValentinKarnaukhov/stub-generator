package com.github.valentinkarnaukhov.stubgenerator;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;

public class TestMock {

    public static void mock(StubMapping stubMapping){
        System.out.println(stubMapping.getName());
    }

    public String hello(){
        return "hello";
    }

}
