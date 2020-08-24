package com.github.valentinkarnaukhov.stubgenerator;

import com.github.valentinkarnaukhov.stubgenerator.api.BodyParamsPostApi;
import com.github.valentinkarnaukhov.stubgenerator.model.CompositeBodyParam;
import com.github.valentinkarnaukhov.stubgenerator.model.CompositeFieldParam;
import com.github.valentinkarnaukhov.stubgenerator.stub.BodyParamsPostMock;
import com.github.valentinkarnaukhov.stubgenerator.util.Wiremock;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

public class BodyParamParsingTest {

    private final BodyParamsPostApi postApi = new BodyParamsPostApi();

    @BeforeAll
    public static void beforeAll() {
        Wiremock.instance.start();
    }

    @BeforeEach
    public void beforeEach() {
        Wiremock.instance.resetAll();
    }

    @Test
    public void primitiveBodyMatchShouldReturn200() throws ApiException {
        BodyParamsPostMock.PostObjectPrimitive mock = new BodyParamsPostMock.PostObjectPrimitive();
        final String requestBody = "REQUEST STRING BODY";

        mock.inBody(requestBody)
                .code200()
                .inResp("MATCHED")
                .mock();

        String response = postApi.postObjectPrimitive(requestBody);

        Assertions.assertEquals(200, postApi.getApiClient().getStatusCode());
        Assertions.assertEquals("MATCHED", response);
    }

    @Test
    public void primitiveListBodyMatchShouldReturn200() throws ApiException {
        BodyParamsPostMock.PostObjectPrimitiveList mock = new BodyParamsPostMock.PostObjectPrimitiveList();
        final List<String> requestBody = new ArrayList<>();
        requestBody.add("VALUE_1");
        requestBody.add("VALUE_2");

        BodyParamsPostMock.PostObjectPrimitiveList.StringBuilder<BodyParamsPostMock.PostObjectPrimitiveList> builder = mock.inBody();
        requestBody.forEach(builder::addNew);

        builder.exit()
                .code200()
                .inResp("MATCHED")
                .mock();

        String response = postApi.postObjectPrimitiveList(requestBody);

        Assertions.assertEquals(200, postApi.getApiClient().getStatusCode());
        Assertions.assertEquals("MATCHED", response);
    }

    @Test
    public void compositeBodyMatchShouldReturn200() throws ApiException {
        BodyParamsPostMock.PostObjectComposite mock = new BodyParamsPostMock.PostObjectComposite();
        final CompositeBodyParam requestBody = new CompositeBodyParam()
                .primitive("PRIMITIVE FIELD")
                .composite(new CompositeFieldParam().innerField("COMPOSITE FIELD"))
                .addPrimitiveListItem("PRIMITIVE LIST FIELD 1")
                .addPrimitiveListItem("PRIMITIVE LIST FIELD 2")
                .addCompositeListItem(new CompositeFieldParam().innerField("COMPOSITE LIST FIELD 1"))
                .addCompositeListItem(new CompositeFieldParam().innerField("COMPOSITE LIST FIELD 2"));

        mock.inBodyPrimitive(requestBody.getPrimitive())
                .inBodyPrimitiveList()
                .addNew("PRIMITIVE LIST FIELD 1").addNew("PRIMITIVE LIST FIELD 2").exit()
                .inBodyComposite(requestBody.getComposite())
                .inBodyCompositeList()
                .addNew().inBodyInnerField("COMPOSITE LIST FIELD 1")
                .addNew().inBodyInnerField("COMPOSITE LIST FIELD 2").exit()
                .code200()
                .inResp("MATCHED")
                .mock();

        String response = postApi.postObjectComposite(requestBody);

        Assertions.assertEquals(200, postApi.getApiClient().getStatusCode());
        Assertions.assertEquals("MATCHED", response);
    }

    @Test
    public void compositeListBodyMatchShouldReturn200() throws ApiException {
        BodyParamsPostMock.PostObjectCompositeList mock = new BodyParamsPostMock.PostObjectCompositeList();
        final CompositeBodyParam requestBodyItem = new CompositeBodyParam()
                .primitive("PRIMITIVE FIELD")
                .composite(new CompositeFieldParam().innerField("COMPOSITE FIELD"))
                .addPrimitiveListItem("PRIMITIVE LIST FIELD 1")
                .addPrimitiveListItem("PRIMITIVE LIST FIELD 2")
                .addCompositeListItem(new CompositeFieldParam().innerField("COMPOSITE LIST FIELD 1"))
                .addCompositeListItem(new CompositeFieldParam().innerField("COMPOSITE LIST FIELD 2"));

        final List<CompositeBodyParam> requestBody = new ArrayList<>();
        requestBody.add(requestBodyItem);

        mock.inBody()
                .addNew()
                .inBodyPrimitive(requestBodyItem.getPrimitive())
                .inBodyPrimitiveList().addNew("PRIMITIVE LIST FIELD 1").addNew("PRIMITIVE LIST FIELD 2").exit()
                .inBodyComposite(requestBodyItem.getComposite())
                .inBodyCompositeList()
                .addNew().inBodyInnerField("COMPOSITE LIST FIELD 1").addNew().inBodyInnerField("COMPOSITE LIST FIELD 2").exit()
                .exit()
                .code200()
                .inResp("MATCHED")
                .mock();

        String response = postApi.postObjectCompositeList(requestBody);

        Assertions.assertEquals(200, postApi.getApiClient().getStatusCode());
        Assertions.assertEquals("MATCHED", response);
    }
}
