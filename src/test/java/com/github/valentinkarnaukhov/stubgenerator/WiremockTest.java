package com.github.valentinkarnaukhov.stubgenerator;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.valentinkarnaukhov.stubgenerator.stub.TestTagPostMock;
import io.swagger.client.ApiException;
import io.swagger.client.api.TestTagPostApi;
import io.swagger.client.model.BodyParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
                .inBody("B1")
                .code200()
                    .inRespParam1("R1")
                    .inRespParam2()
                        .addNew("R2")
                        .addNew("R3")
                        .exit()
                    .inRespParam3_Inner1("R4")
                    .inRespParam4()
                        .addNew()
                            .inRespInner1("R5")
                        .exit()
                    .mock();



        TestTagPostApi testTagApi = new TestTagPostApi();
        List<String> qp = new ArrayList<>();
        qp.add("Q1");
        BodyParam bodyParam = testTagApi.postObject("0", qp, "Q2");

        System.out.println(bodyParam);
    }
}
