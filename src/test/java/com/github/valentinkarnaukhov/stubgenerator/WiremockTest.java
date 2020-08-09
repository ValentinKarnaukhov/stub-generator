//package com.github.valentinkarnaukhov.stubgenerator;
//
//import com.github.tomakehurst.wiremock.WireMockServer;
//import com.github.valentinkarnaukhov.stubgenerator.stub.TestTagMock;
//import io.swagger.client.ApiException;
//import io.swagger.client.api.TestTagApi;
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
//        TestTagMock.GetObjectByParams mock = new TestTagMock.GetObjectByParams();
//        mock
//                .inQueryParam1("1")
//                .inQueryParam2(1L)
//                .code200()
//                .withObjectParam1(123L)
//                .withObjectParam2("Str")
//                .withObjectParam3(true).mock();
//
//        TestTagApi testTagApi = new TestTagApi();
//        TestObject testObject = testTagApi.getObjectByParams("1", 1L);
//
//        System.out.println(testObject);
//    }
//}