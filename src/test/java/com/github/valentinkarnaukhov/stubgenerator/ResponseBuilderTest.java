//package com.github.valentinkarnaukhov.stubgenerator;
//
//import com.github.tomakehurst.wiremock.common.Json;
//import com.github.valentinkarnaukhov.stubgenerator.api.QueryParamsGetResponseApi;
//import com.github.valentinkarnaukhov.stubgenerator.model.CompositeBodyParam;
//import com.github.valentinkarnaukhov.stubgenerator.model.CompositeFieldParam;
//import com.github.valentinkarnaukhov.stubgenerator.stub.QueryParamsGetResponseMock;
//import com.github.valentinkarnaukhov.stubgenerator.util.Wiremock;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//public class ResponseBuilderTest {
//
//    private final QueryParamsGetResponseApi getResponseApi = new QueryParamsGetResponseApi();
//
//    @BeforeAll
//    public static void beforeAll() {
//        Wiremock.instance.start();
//    }
//
//    @BeforeEach
//    public void beforeEach() {
//        Wiremock.instance.resetAll();
//    }
//
//    @Test
//    public void shouldReturnPrimitive200() throws ApiException {
//        QueryParamsGetResponseMock.GetWithResponse mock = new QueryParamsGetResponseMock.GetWithResponse();
//
//        mock.inQueryStingParam("TEST").code200().inResp("PRIMITIVE").mock();
//
//        String response = getResponseApi.getWithResponse("TEST");
//
//        Assertions.assertEquals(200, getResponseApi.getApiClient().getStatusCode());
//        Assertions.assertEquals("PRIMITIVE", response);
//    }
//
//    @Test
//    public void shouldReturnPrimitiveList201() throws ApiException {
//        QueryParamsGetResponseMock.GetWithResponse mock = new QueryParamsGetResponseMock.GetWithResponse();
//        List<String> expected = new ArrayList<>();
//        expected.add("VALUE_1");
//        expected.add("VALUE_2");
//
//        mock.inQueryStingParam("TEST").code201().inResp().addNew("VALUE_1").addNew("VALUE_2").exit().mock();
//
//        String response = getResponseApi.getWithResponse("TEST");
//
//        Assertions.assertEquals(201, getResponseApi.getApiClient().getStatusCode());
//        Assertions.assertEquals(expected, Json.read(response, List.class));
//    }
//
//    @Test
//    public void shouldReturnComposite202() throws ApiException {
//        QueryParamsGetResponseMock.GetWithResponse mock = new QueryParamsGetResponseMock.GetWithResponse();
//        CompositeBodyParam expected = new CompositeBodyParam();
//        expected.setPrimitive("PRIMITIVE");
//        expected.setComposite(new CompositeFieldParam().innerField("INNER PRIMITIVE"));
//        expected.setPrimitiveList(Arrays.asList("VALUE_1", "VALUE_2"));
//        expected.setCompositeList(Arrays.asList(new CompositeFieldParam().innerField("INNER_1"), new CompositeFieldParam().innerField("INNER_2")));
//
//        mock.inQueryStingParam("TEST").code202()
//                .inRespPrimitive("PRIMITIVE")
//                .inRespPrimitiveList().addNew("VALUE_1").addNew("VALUE_2").exit()
//                .inRespComposite(new CompositeFieldParam().innerField("INNER PRIMITIVE"))
//                .inRespCompositeList().addNew().inBodyInnerField("INNER_1").addNew().inBodyInnerField("INNER_2").exit()
//                .mock();
//
//        String response = getResponseApi.getWithResponse("TEST");
//
//        Assertions.assertEquals(202, getResponseApi.getApiClient().getStatusCode());
//        Assertions.assertEquals(expected, Json.read(response, CompositeBodyParam.class));
//    }
//
//    @Test
//    public void shouldReturnCompositeList203() throws ApiException {
//        QueryParamsGetResponseMock.GetWithResponse mock = new QueryParamsGetResponseMock.GetWithResponse();
//        CompositeBodyParam expected = new CompositeBodyParam();
//        expected.setPrimitive("PRIMITIVE");
//        expected.setComposite(new CompositeFieldParam().innerField("INNER PRIMITIVE"));
//        expected.setPrimitiveList(Arrays.asList("VALUE_1", "VALUE_2"));
//        expected.setCompositeList(Arrays.asList(new CompositeFieldParam().innerField("INNER_1"), new CompositeFieldParam().innerField("INNER_2")));
//
//        mock.inQueryStingParam("TEST").code203()
//                .inResp().addNew()
//                .inRespPrimitive("PRIMITIVE")
//                .inRespPrimitiveList().addNew("VALUE_1").addNew("VALUE_2").exit()
//                .inRespComposite(new CompositeFieldParam().innerField("INNER PRIMITIVE"))
//                .inRespCompositeList().addNew().inBodyInnerField("INNER_1").addNew().inBodyInnerField("INNER_2").exit()
//                .exit().mock();
//
//        String response = getResponseApi.getWithResponse("TEST");
//
//        Assertions.assertEquals(203, getResponseApi.getApiClient().getStatusCode());
//        Assertions.assertEquals(Json.write(Collections.singletonList(expected)), response);
//    }
//}
