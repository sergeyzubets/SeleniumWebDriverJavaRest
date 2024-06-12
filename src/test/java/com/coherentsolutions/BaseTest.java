package com.coherentsolutions;

import com.coherentsolutions.clients.HttpClient;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


@Slf4j
public class BaseTest {
    protected CloseableHttpClient httpClient;

    protected static final String RESPONSE_CODE_FAILURE = "Response code is not valid.";

    @BeforeEach
    @Step("Get http client instance and access tokens.")
    protected void environmentSetup() {
        httpClient = HttpClient.getHttpClientInstance();
    }

    @AfterEach
    @Step("The environment tear down.")
    protected void tearDown() {
        HttpClient.closeHttpClient();
    }
}