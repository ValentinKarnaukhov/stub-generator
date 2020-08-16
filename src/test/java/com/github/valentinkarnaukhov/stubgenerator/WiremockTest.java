//package com.github.valentinkarnaukhov.stubgenerator;
//
//import com.github.tomakehurst.wiremock.WireMockServer;
//import com.github.valentinkarnaukhov.stubgenerator.stub.TestTagPostMock;
//import io.swagger.client.ApiException;
//import io.swagger.client.api.TestTagPostApi;
//import io.swagger.client.model.BodyParam;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
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
//        TestTagPostMock.PostObject postMock = new TestTagPostMock.PostObject();
//        postMock.inQueryParam1()
//                .addNew("1")
//                .addNew("2")
//                .exit()
//                .inQueryParam2("3")
//                .code200()
//                .mock();
//
//        TestTagPostApi testTagApi = new TestTagPostApi();
//        List<String> qp = new ArrayList<>();
//        qp.add("1");
//        qp.add("2");
//        BodyParam bodyParam = testTagApi.postObject("0", qp, "3");
//
//        System.out.println(bodyParam);
//    }
//}