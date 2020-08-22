package com.github.valentinkarnaukhov.stubgenerator;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.valentinkarnaukhov.stubgenerator.stub.TestTagPostMock;
import io.swagger.client.ApiException;
import io.swagger.client.api.TestTagPostApi;
import io.swagger.client.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class WiremockTest {

    @BeforeEach
    public void before() {
        new WireMockServer(8080).start();
    }

    @Test
    public void simpleCallToMock() throws ApiException {
        TestTagPostMock.PostObject postMock = new TestTagPostMock.PostObject();
        postMock
                .inQueryParam1()
                    .addNew("Q1")
                    .exit()
                .inQueryParam2("Q2")
                .inBody("TEST")
                .code200()
                    .inRespParam1("R1")
                    .inRespParam2()
                        .addNew("R2")
                        .addNew("R3")
                        .exit()
                    .inRespParam3_Inner1("R4")
                    .inRespParam4()
                        .addNew()
                        .exit()
                    .mock();



        TestTagPostApi testTagApi = new TestTagPostApi();
        List<String> qp = new ArrayList<>();
        qp.add("Q1");
        ListBody listBody = new ListBody();
        listBody.setParam1(new ArrayList<>());
        listBody.getParam1().add(new ListParam().param2("B1"));
        BodyParam bodyParam = testTagApi.postObject("TEST", qp, "Q2");

        System.out.println(bodyParam);
    }
}
