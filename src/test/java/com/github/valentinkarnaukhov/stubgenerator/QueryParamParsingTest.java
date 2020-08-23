package com.github.valentinkarnaukhov.stubgenerator;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.valentinkarnaukhov.stubgenerator.api.QueryParamsGetApi;
import com.github.valentinkarnaukhov.stubgenerator.stub.QueryParamsGetMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class QueryParamParsingTest {

    private final QueryParamsGetApi getApi = new QueryParamsGetApi();

    @BeforeAll
    public static void beforeAll() {
        new WireMockServer(8080).start();
    }

    @Test
    public void allQueryParamsMatchedShouldReturn200() throws ApiException {
        QueryParamsGetMock.GetObjectByParams mock = new QueryParamsGetMock.GetObjectByParams();
        mock.inQueryStingParam("STRING")
                .inQueryIntegerParam(123)
                .inQueryLongParam(123L)
                .inQueryFloatParam(123.123f)
                .inQueryDoubleParam(123.123)
                .inQueryBoolParam(true)
                .inQueryEnumParam("EV1")
                .code200()
                .inResp("MATCHED")
                .mock();

        String response = getApi.getObjectByParams("STRING", 123, 123L, true,
                123.123f, 123.123, "EV1");
        Assertions.assertEquals(200, getApi.getApiClient().getStatusCode());
        Assertions.assertEquals("MATCHED", response);
    }

}
