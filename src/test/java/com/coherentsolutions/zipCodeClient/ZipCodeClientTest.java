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

import static com.coherentsolutions.data.ErrorMessages.Common.RESPONSE_CODE_FAILURE;
import static org.junit.jupiter.api.Assertions.*;

@Epic("REST API test.")
@Feature("Zip Code controller test")
public class ZipCodeClientTest extends BaseTest {

    @Issue("Response code is not valid: 201 instead of 200.")
    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Get all available zip codes test")
    @Description("Scenario #1: The test verifies ability to get all available zip codes in the application for now.")
    @Test
    public void getAvailableZipCodesTest() {
        ZipCodeDTO newZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        zipCodeClient.createZipCodes(httpClient, List.of(newZipCode), HttpStatus.SC_CREATED);

        int expectedResponseCode = HttpStatus.SC_OK;
        Response response = zipCodeClient.getZipCodes(httpClient, expectedResponseCode);
        List<?> actualZipCodes = response.getParsedBody();

        assertAll("GET all available zip codes test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertTrue(actualZipCodes.contains(newZipCode), "List of available zip codes does not contain added one.")
        );
    }

    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Expand zip codes test")
    @Description("Scenario #2: The test verifies ability to add new zip codes to the application.")
    @Test
    public void expandZipCodesTest() {
        ZipCodeDTO zipCodeToAdd1 = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        ZipCodeDTO zipCodeToAdd2 = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        List<ZipCodeDTO> zipCodesToAdd = List.of(zipCodeToAdd1, zipCodeToAdd2);

        int expectedResponseCode = HttpStatus.SC_CREATED;
        Response response = zipCodeClient.createZipCodes(httpClient, zipCodesToAdd, expectedResponseCode);
        List<?> actualZipCodes = response.getParsedBody();

        assertAll("Expand zip codes test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertTrue(actualZipCodes.contains(zipCodeToAdd1),
                        "Zip code " + zipCodeToAdd1 + " has not been added."),
                () -> assertTrue(actualZipCodes.contains(zipCodeToAdd2),
                        String.format("Zip code %s has not been added.", zipCodeToAdd2))
        );
    }

    @Issue("Duplicates for available zip codes can be added to the application.")
    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Add duplicates for available zip codes test")
    @Description("Scenario #3: The test verifies that duplicates for available zip codes cannot be added to the application.")
    @Test
    public void addDuplicatesForAvailableZipCodesTest() {
        ZipCodeDTO duplicate1 = zipCodeClientBO.getDuplicate(httpClient, HttpStatus.SC_OK);
        ZipCodeDTO duplicate2 = zipCodeClientBO.getDuplicate(httpClient, HttpStatus.SC_OK);
        ZipCodeDTO uniqueZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        List<ZipCodeDTO> duplicates = List.of(duplicate1, duplicate2, uniqueZipCode);

        int expectedResponseCode = HttpStatus.SC_CREATED;
        Response response = zipCodeClient.createZipCodes(httpClient, duplicates, expectedResponseCode);
        List<?> actualZipCodes = response.getParsedBody();

        assertAll("Add duplicates for available zip codes test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertTrue(actualZipCodes.contains(uniqueZipCode), "List of available zip codes does not contain added one."),
                () -> assertFalse(actualZipCodes.contains(duplicate1),
                        String.format("Duplicate for existing code %s was added to the application.", duplicate1)),
                () -> assertFalse(actualZipCodes.contains(duplicate2),
                        String.format("Duplicate for existing code %s was added to the application.", duplicate2))
        );
    }

    @Issue("Duplicates for used zip codes can be added to the application.")
    @Story("Task 20 - Get available zip codes and add more to the list.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Add duplicates for used zip codes test")
    @Description("Scenario #4: The test verifies that duplicates of available and used zip codes cannot be added to the application.")
    @Test
    public void addDuplicatedForUsedZipCodesTest() {
        ZipCodeDTO uniqueZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        ZipCodeDTO usedZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        zipCodeClient.createZipCodes(httpClient, List.of(usedZipCode), HttpStatus.SC_CREATED);
        List<ZipCodeDTO> zipCodesToAdd = List.of(usedZipCode, uniqueZipCode);

        int expectedResponseCode = HttpStatus.SC_CREATED;
        UserDTO userToAdd = userClientBO.getUniqueUser(httpClient, usedZipCode, HttpStatus.SC_OK);
        userClient.createUser(httpClient, userToAdd, expectedResponseCode);

        Response response = zipCodeClient.createZipCodes(httpClient, zipCodesToAdd, expectedResponseCode);
        List<?> actualZipCodes = response.getParsedBody();

        assertAll("Add duplicates for used zip codes test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertTrue(actualZipCodes.contains(uniqueZipCode), "List of available zip codes does not contain added one."),
                () -> assertFalse(actualZipCodes.contains(usedZipCode),
                        String.format("Duplicate for used code %s was added to the application.", usedZipCode))
        );
    }
}