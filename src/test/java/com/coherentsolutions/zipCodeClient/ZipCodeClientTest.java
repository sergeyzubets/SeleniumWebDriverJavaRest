package com.coherentsolutions.zipCodeClient;

import com.coherentsolutions.BaseTest;
import com.coherentsolutions.data.dto.UserDTO;
import com.coherentsolutions.data.dto.ZipCodeDTO;
import com.coherentsolutions.data.models.Response;
import io.qameta.allure.*;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Zip Code Controller test")
public class ZipCodeClientTest extends BaseTest {

    @Issue("Response code is not valid: 201 instead of 200.")
    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Get all available zip codes test")
    @Description("Scenario #1: The test verifies ability to get all available zip codes in the application for now.")
    @Test
    public void getAvailableZipCodesTest() {
        ZipCodeDTO newZipCode = zipCodeClientBO.addUniqueZipCode(httpClient);
        Response response = zipCodeClient.getZipCodes(httpClient);
        List<?> actualZipCodes = response.getParsedBody();

        assertAll("GET all available zip codes test failed.",
                () -> assertEquals(HttpStatus.SC_OK, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertTrue(actualZipCodes.contains(newZipCode), "List of available zip codes does not contain added one."));
    }

    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Expand Zip Codes Test")
    @Description("Scenario #2: The test verifies ability to add new zip codes to the application.")
    @Test
    public void expandZipCodesTest() {
        ZipCodeDTO zipCodeToAdd1 = zipCodeClientBO.getNonexistentZipCode(httpClient);
        ZipCodeDTO zipCodeToAdd2 = zipCodeClientBO.getNonexistentZipCode(httpClient);
        List<ZipCodeDTO> zipCodesToAdd = List.of(zipCodeToAdd1, zipCodeToAdd2);

        Response response = zipCodeClient.createZipCodes(httpClient, zipCodesToAdd);
        List<?> actualZipCodes = response.getParsedBody();

        assertAll("Expand zip codes test failed.",
                () -> assertEquals(HttpStatus.SC_CREATED, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertTrue(actualZipCodes.contains(zipCodeToAdd1),
                        "Zip code " + zipCodeToAdd1 + " has not been added."),
                () -> assertTrue(actualZipCodes.contains(zipCodeToAdd2),
                        "Zip code " + zipCodeToAdd2 + " has not been added."));
    }

    @Issue("Duplicates for available zip codes can be added to the application.")
    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Add duplicates for available zip codes test")
    @Description("Scenario #3: The test verifies that duplicates for available zip codes cannot be added to the application.")
    @Test
    public void addDuplicatesForAvailableZipCodesTest() {
        ZipCodeDTO duplicate1 = zipCodeClientBO.getDuplicate(httpClient);
        ZipCodeDTO duplicate2 = zipCodeClientBO.getDuplicate(httpClient);
        ZipCodeDTO uniqueZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient);
        List<ZipCodeDTO> duplicates = List.of(duplicate1, duplicate2, uniqueZipCode);

        Response response = zipCodeClient.createZipCodes(httpClient, duplicates);
        List<?> actualZipCodes = response.getParsedBody();

        assertAll("Add duplicates for available zip codes test failed.",
                () -> assertEquals(response.getCode(), HttpStatus.SC_CREATED, RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualZipCodes.contains(duplicate1),
                        "Duplicate for existing code " + duplicate1 + " was added to the application."),
                () -> assertFalse(actualZipCodes.contains(duplicate2),
                        "Duplicate for existing code " + duplicate2 + " was added to the application."));
    }

    @Issue("Duplicates for used zip codes can be added to the application.")
    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Add duplicates for used zip codes test")
    @Description("Scenario #4: The test verifies that duplicates of available and used zip codes cannot be added to the application.")
    @Test
    public void addDuplicatedForUsedZipCodesTest() {
        ZipCodeDTO usedZipCode = zipCodeClientBO.addUniqueZipCode(httpClient);
        ZipCodeDTO zipCode1 = zipCodeClientBO.addUniqueZipCode(httpClient);
        List<ZipCodeDTO> zipCodesToAdd = List.of(usedZipCode, zipCode1);

        UserDTO userToAdd = userClientBO.getUniqueUser(httpClient, usedZipCode);
        userClient.createUser(httpClient, userToAdd);

        Response response = zipCodeClient.createZipCodes(httpClient, zipCodesToAdd);
        List<?> actualZipCodes = response.getParsedBody();

        assertAll("Add duplicates for used zip codes test failed.",
                () -> assertEquals(response.getCode(), HttpStatus.SC_CREATED, RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualZipCodes.contains(usedZipCode),
                        "Duplicate for used code " + usedZipCode + " was added to the application."));
    }
}