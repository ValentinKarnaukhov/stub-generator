package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.api.QueryParamsGetApi;
import com.github.valentinkarnaukhov.stubgenerator.stub.QueryParamsGetMock;
import com.github.valentinkarnaukhov.stubgenerator.util.Wiremock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryParamParsingTest {

    private final QueryParamsGetApi getApi = new QueryParamsGetApi();

    @BeforeAll
    public static void beforeAll() {
        Wiremock.instance.start();
    }

    @BeforeEach
    public void beforeEach() {
        Wiremock.instance.resetAll();
    }

    @Test
    public void allQueryParamsMatchShouldReturn200() throws ApiException {
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

    @Test
    public void mockWithPartlyFilledParamShouldReturn200() throws ApiException {
        QueryParamsGetMock.GetObjectByParams mock = new QueryParamsGetMock.GetObjectByParams();
        mock.inQueryStingParam("STRING")
                .inQueryIntegerParam(123)
                .inQueryBoolParam(true)
                .code200()
                .inResp("MATCHED")
                .mock();

        String response = getApi.getObjectByParams("STRING", 123, 123L, true,
                123.123f, 123.123, "EV1");
        Assertions.assertEquals(200, getApi.getApiClient().getStatusCode());
        Assertions.assertEquals("MATCHED", response);
    }

    @Test
    public void queryParamMismatchShouldNotReturnAny() {
        QueryParamsGetMock.GetObjectByParams mock = new QueryParamsGetMock.GetObjectByParams();
        mock.inQueryStingParam("STRING")
                .inQueryIntegerParam(123)
                .inQueryBoolParam(true)
                .code200()
                .inResp("MATCHED")
                .mock();

        try {
            getApi.getObjectByParams("INCORRECT", 123, null, true,
                    null, null, null);
        } catch (ApiException ignored) {
        }

        Assertions.assertEquals(404, getApi.getApiClient().getStatusCode());
    }
}
