package com.github.valentinkarnaukhov.stubgenerator.util;

import com.github.tomakehurst.wiremock.WireMockServer;

public class Wiremock {

    public static final WireMockServer instance = new WireMockServer(8080);

}
