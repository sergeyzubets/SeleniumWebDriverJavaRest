package com.coherentsolutions.zipCodeClient;

import com.coherentsolutions.BaseTest;
import com.coherentsolutions.clients.UserClient;
import com.coherentsolutions.clients.ZipCodeClient;
import com.coherentsolutions.dto.UserDTO;
import com.coherentsolutions.dto.ZipCodeDTO;
import io.qameta.allure.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.coherentsolutions.utils.UserClientUtil.getUniqueUser;
import static com.coherentsolutions.utils.ZipCodeClientUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@Feature("Zip Code Controller")
public class ZipCodeClientTest extends BaseTest {

    @Issue("Response code is not valid: 201 instead of 200.")
    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Get all available zip codes test")
    @Description("Scenario #1: The test verifies ability to get all available zip codes in the application for now.")
    @Test
    public void getAllAvailableZipCodesTest() {
        ZipCodeDTO newZipCode = addUniqueZipCodeToApp(httpClient, readToken, writeToken);

        ZipCodeClient zipCodeClient = new ZipCodeClient();
        CloseableHttpResponse response = zipCodeClient.sendGetAvailableZipCodesRequest(httpClient, readToken);
        List<ZipCodeDTO> zipCodes = zipCodeClient.getZipCodes();

        assertAll("GET All available zip codes test failed.",
                () -> assertEquals(HttpStatus.SC_OK, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertTrue(zipCodes.contains(newZipCode), "List of available zip codes does not contain added code."));
    }

    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Expand Zip Codes Test")
    @Description("Scenario #2: The test verifies ability to add new zip codes to the application.")
    @Test
    public void expandZipCodesTest() {
        ZipCodeDTO zipCodeToAdd1 = getNonexistentZipCode(httpClient, readToken);
        ZipCodeDTO zipCodeToAdd2 = getNonexistentZipCode(httpClient, readToken);
        List<ZipCodeDTO> zipCodesToAdd = List.of(zipCodeToAdd1, zipCodeToAdd2);

        ZipCodeClient zipCodeClient = new ZipCodeClient();
        CloseableHttpResponse response = zipCodeClient.sendPostExpandZipCodesRequest(httpClient, writeToken, zipCodesToAdd);
        List<ZipCodeDTO> updatedZipCodes = zipCodeClient.getZipCodes();

        assertAll("Expand zip codes test failed.",
                () -> assertEquals(HttpStatus.SC_CREATED, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> zipCodesToAdd.forEach(newZipCode -> assertTrue(updatedZipCodes.contains(newZipCode),
                        "Zip code " + newZipCode + " has not been added.")));
    }

    @Issue("Duplicates for available zip codes can be added to the application.")
    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Add duplicates for available zip codes test")
    @Description("Scenario #3: The test verifies that duplicates for available zip codes cannot be added to the application.")
    @Test
    public void addDuplicatesForAvailableZipCodesTest() {
        ZipCodeDTO duplicate1 = getDuplicatedAvailableZipCode(httpClient, readToken);
        ZipCodeDTO duplicate2 = getDuplicatedAvailableZipCode(httpClient, readToken);
        ZipCodeDTO uniqueZipCode = getNonexistentZipCode(httpClient, readToken);
        List<ZipCodeDTO> duplicates = List.of(duplicate1, duplicate2, uniqueZipCode);

        ZipCodeClient zipCodeClient = new ZipCodeClient();
        CloseableHttpResponse response = zipCodeClient.sendPostExpandZipCodesRequest(httpClient, writeToken, duplicates);
        List<ZipCodeDTO> updatedListOfZipCodes = zipCodeClient.getZipCodes();

        assertAll("Add duplicates for available zip codes test failed.",
                () -> assertEquals(response.getCode(), HttpStatus.SC_CREATED, RESPONSE_CODE_FAILURE),
                () -> duplicates.forEach(zipCode -> assertEquals(1, getZipCodeRepetitionCount(updatedListOfZipCodes, zipCode),
                        "Duplicate for existing code " + zipCode + " was added.")));
    }

    @Issue("Duplicates for used zip codes can be added to the application.")
    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Add duplicates for used zip codes test")
    @Description("Scenario #4: The test verifies that duplicates of available and used zip codes cannot be added to the application.")
    @Test
    public void addDuplicatedForUsedZipCodesTest() {
        ZipCodeDTO usedZipCode = addUniqueZipCodeToApp(httpClient, readToken, writeToken);
        ZipCodeDTO usedZipCode2 = addUniqueZipCodeToApp(httpClient, readToken, writeToken);
        ZipCodeDTO usedZipCode3 = getNonexistentZipCode(httpClient, readToken);
        List<ZipCodeDTO> zipCodesToAdd = List.of(usedZipCode, usedZipCode2, usedZipCode3);

        UserDTO userToAdd = getUniqueUser(httpClient, readToken, usedZipCode);
        new UserClient().sendPostCreateUserRequest(httpClient, writeToken, userToAdd);

        ZipCodeClient zipCodeClient = new ZipCodeClient();
        CloseableHttpResponse response = zipCodeClient.sendPostExpandZipCodesRequest(httpClient, writeToken, zipCodesToAdd);
        List<ZipCodeDTO> updatedListOfZipCodes = zipCodeClient.getZipCodes();

        assertAll("Add duplicates for used zip codes test failed.",
                () -> assertEquals(response.getCode(), HttpStatus.SC_CREATED, RESPONSE_CODE_FAILURE),
                () -> zipCodesToAdd.forEach(zipCode -> assertEquals(1, getZipCodeRepetitionCount(updatedListOfZipCodes, zipCode),
                        "Duplicate for used code " + zipCode + " was added.")));
    }
}