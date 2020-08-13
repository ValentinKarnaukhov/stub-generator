//package com.github.valentinkarnaukhov.stubgenerator;
//
//import com.github.tomakehurst.wiremock.WireMockServer;
//import com.github.valentinkarnaukhov.stubgenerator.stub.TestTagPostMock;
//import io.swagger.client.ApiException;
//import io.swagger.client.api.TestTagPostApi;
//import io.swagger.client.model.TestObject;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//class WiremockTest {
//
//    @BeforeEach
//    public void before() {
//        new WireMockServer(8080).start();
//    }
//
//    @Test
//    public void simpleCallToMock() throws ApiException {
//        TestTagPostMock.PostObject postObject = new TestTagPostMock.PostObject();
//
//        postObject
//                .inBodyLevel1Param1("123")
//                .code200()
//                .withObjectParam1_ChildObjectParam1("test")
//                .exit().mock();
//
//    }
//}