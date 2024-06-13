package com.coherentsolutions;

import com.coherentsolutions.clients.HttpClient;
import com.coherentsolutions.clients.UserClient;
import com.coherentsolutions.clients.ZipCodeClient;
import com.coherentsolutions.data.businessObjects.UserClientBO;
import com.coherentsolutions.data.businessObjects.ZipCodeClientBO;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;


@Slf4j
public class BaseTest {
    protected static final String RESPONSE_CODE_FAILURE = "Response code is not valid.";
    protected CloseableHttpClient httpClient;
    protected ZipCodeClient zipCodeClient;
    protected UserClient userClient;
    protected ZipCodeClientBO zipCodeClientBO;
    protected UserClientBO userClientBO;

    public BaseTest() {
        zipCodeClient = new ZipCodeClient();
        userClient = new UserClient();
        zipCodeClientBO = new ZipCodeClientBO();
        userClientBO = new UserClientBO();
    }

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

    @AfterAll
    public static void setAllureEnvironment() {
        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("Docker image scheme", System.getProperty("scheme"))
                        .put("Docker image host", System.getProperty("host"))
                        .put("Docker image port", System.getProperty("port"))
                        .build());
    }
}