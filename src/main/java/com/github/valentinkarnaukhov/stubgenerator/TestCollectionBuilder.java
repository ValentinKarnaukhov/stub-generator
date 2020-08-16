//package com.github.valentinkarnaukhov.stubgenerator;
//
//import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern;
//import com.github.valentinkarnaukhov.stubgenerator.model.TestObj;
//import com.github.valentinkarnaukhov.stubgenerator.stub.TestTagPostMock;
//import com.github.valentinkarnaukhov.stubgenerator.support.CollectionBuilder;
//import com.github.valentinkarnaukhov.stubgenerator.support.PrimitiveCollectionBuilder;
//
//import java.util.List;
//
//import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
//
//public class TestCollectionBuilder extends PrimitiveCollectionBuilder<TestTagPostMock.PostObject, String> {
//
//    public TestCollectionBuilder(TestTagPostMock.PostObject enter) {
//        super(enter);
//    }
//
//    public TestCollectionBuilder inBodyPrimParam(String primParam) {
//        return this;
//    }
//
//
//    public TestCollectionBuilder inBodyObjParamField(String objParamField) {
//        return this;
//    }
//
//}
