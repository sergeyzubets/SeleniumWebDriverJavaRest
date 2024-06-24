package com.coherentsolutions;

import com.coherentsolutions.clients.HttpClient;
import com.coherentsolutions.clients.UserClient;
import com.coherentsolutions.clients.ZipCodeClient;
import com.coherentsolutions.data.businessObjects.UserClientBO;
import com.coherentsolutions.data.businessObjects.ZipCodeClientBO;
import com.coherentsolutions.data.dto.UserDTO;
import com.coherentsolutions.data.models.Parameter;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static com.coherentsolutions.utils.GeneralUtil.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {
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

    protected static List<UserDTO> missedRequiredFieldsUser() {
        return List.of(
                new UserDTO(getRandomUserName(), null),
                new UserDTO(null, getRandomGender()),
                new UserDTO(null, null)
        );
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
    public void tearDown() {
        usersCleanUp();
        writeAllureEnvironmentFile(
                List.of(
                        new Parameter("Docker image scheme", System.getProperty("scheme")),
                        new Parameter("Docker image host", System.getProperty("host")),
                        new Parameter("Docker image port", System.getProperty("port"))
                )
        );
    }

    private void usersCleanUp() {
        log.info("Removing all added users.");
        CloseableHttpClient client = HttpClient.getHttpClientInstance();
        userClientBO.getCreatedUsers(client, HttpStatus.SC_OK)
                .forEach(user -> userClient.deleteUser(client, user, HttpStatus.SC_NO_CONTENT));
    }
}