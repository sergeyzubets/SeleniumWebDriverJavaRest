package com.coherentsolutions;

import com.coherentsolutions.clients.AuthorizationClient;
import com.coherentsolutions.clients.HttpClient;
import com.coherentsolutions.utils.Scope;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
public class BaseTest {
    protected String readToken;
    protected String writeToken;
    protected CloseableHttpClient httpClient;

    @BeforeEach
    @Step("Get http client instance and access tokens.")
    protected void environmentSetup() throws URISyntaxException, IOException {
        httpClient = HttpClient.getHttpClientInstance();
        AuthorizationClient authorizationClient = new AuthorizationClient();
        readToken = authorizationClient.getAccessToken(httpClient, Scope.READ);
        writeToken = authorizationClient.getAccessToken(httpClient, Scope.WRITE);
    }

    @AfterEach
    @Step("The environment tear down.")
    protected void tearDown() {
        HttpClient.closeHttpClient();
    }
}