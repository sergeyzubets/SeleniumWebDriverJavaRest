package com.coherentsolutions;

import com.coherentsolutions.clients.HttpClient;
import com.coherentsolutions.clients.UserClient;
import com.coherentsolutions.clients.ZipCodeClient;
import com.coherentsolutions.data.businessObjects.UserClientBO;
import com.coherentsolutions.data.businessObjects.ZipCodeClientBO;
import com.coherentsolutions.data.dto.UserDTO;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static com.coherentsolutions.utils.GeneralUtil.getRandomGender;
import static com.coherentsolutions.utils.GeneralUtil.getRandomUserName;
import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;


@Slf4j
public class BaseTest {
    protected CloseableHttpClient httpClient;
    protected ZipCodeClient zipCodeClient;
    protected UserClient userClient;
    protected ZipCodeClientBO zipCodeClientBO;
    protected UserClientBO userClientBO;
    protected static final String RESPONSE_CODE_FAILURE = "Response code is not valid.";
    protected static final String ERROR_MESSAGE_FAILURE = "Error message is not correct.";

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
    @Step("Close Http Client instance.")
    protected void closeClientInstance() {
        HttpClient.closeHttpClient();
    }

    @AfterAll
    public static void tearDown() {
        //TODO add user clean up method as part of tear down
        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("Docker image scheme", System.getProperty("scheme"))
                        .put("Docker image host", System.getProperty("host"))
                        .put("Docker image port", System.getProperty("port"))
                        .build());
    }

    protected static List<UserDTO> missedRequiredFieldsUser() {
        return List.of(
                new UserDTO(getRandomUserName(), null),
                new UserDTO(null, getRandomGender()),
                new UserDTO(null, null)
        );
    }
}