package com.coherentsolutions.userClient;

import com.coherentsolutions.BaseTest;
import com.coherentsolutions.data.dto.UserDTO;
import com.coherentsolutions.data.dto.ZipCodeDTO;
import com.coherentsolutions.data.models.FailedResponseBody;
import com.coherentsolutions.data.models.Response;
import io.qameta.allure.*;
import jdk.jfr.Description;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("REST API test.")
@Feature("User Controller test")
public class CreateUserTest extends BaseTest {
    static final String USERNAME_FAILURE = "Actual and expected Usernames are not the same.";
    static final String AGE_FAILURE = "Actual and expected Ages are not the same.";
    static final String GENDER_FAILURE = "Actual and expected Genders are not the same.";
    static final String ZIP_CODE_FAILURE = "Actual and expected Zip codes are not the same.";
    static final String BODY_FAILURE = "Actual and expected Response bodies are not the same.";

    @Story("Task 30 - Create user.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Create user with all fields populated test")
    @Description("Scenario #1: The test verifies ability to add user to the application with all fields populated. " +
            "Also, the test verifies removing of used zip code from the application.")
    @Test
    public void addUserAllFieldsPopulatedTest() {
        ZipCodeDTO usedZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        zipCodeClient.createZipCodes(httpClient, List.of(usedZipCode), HttpStatus.SC_CREATED);
        UserDTO newUser = userClientBO.getUniqueUser(httpClient, usedZipCode, HttpStatus.SC_OK);

        int expectedResponseCode = HttpStatus.SC_CREATED;
        Response response = userClient.createUser(httpClient, newUser, expectedResponseCode);
        List<ZipCodeDTO> actualZipCodes = zipCodeClientBO.getAvailableZipCodes(httpClient, HttpStatus.SC_OK);
        List<UserDTO> actualUsersList = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        UserDTO actualUser = actualUsersList.stream()
                .filter(user -> user.equals(newUser))
                .findFirst()
                .orElse(new UserDTO());

        assertAll("Create user with all fields populated test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(newUser.getName(), actualUser.getName(), USERNAME_FAILURE),
                () -> assertEquals(newUser.getGender(), actualUser.getGender(), GENDER_FAILURE),
                () -> assertEquals(newUser.getAge(), actualUser.getAge(), AGE_FAILURE),
                () -> assertEquals(newUser.getZipCode(), actualUser.getZipCode(), ZIP_CODE_FAILURE),
                () -> assertFalse(actualZipCodes.contains(usedZipCode),
                        "Used Zip Code has not been removed from available zip codes of application."),
                () -> assertNull(response.getBody(), BODY_FAILURE));
    }

    @Story("Task 30 - Create user.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Create user with mandatory fields populated test")
    @Description("Scenario #2: The test verifies ability to add user to the application with mandatory fields populated.")
    @Test
    public void addUserRequiredFieldsPopulatedTest() {
        UserDTO newUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        int expectedResponseCode = HttpStatus.SC_CREATED;
        Response response = userClient.createUser(httpClient, newUser, expectedResponseCode);
        List<UserDTO> actualUsersList = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        UserDTO actualUser = actualUsersList.stream()
                .filter(user -> user.equals(newUser))
                .findFirst()
                .orElse(new UserDTO());

        assertAll("Create user with mandatory fields populated test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(newUser.getName(), actualUser.getName(), USERNAME_FAILURE),
                () -> assertEquals(newUser.getGender(), actualUser.getGender(), GENDER_FAILURE),
                () -> assertEquals(newUser.getAge(), actualUser.getAge(), AGE_FAILURE),
                () -> assertEquals(newUser.getZipCode(), actualUser.getZipCode(), ZIP_CODE_FAILURE),
                () -> assertNull(response.getBody(), BODY_FAILURE));
    }

    @Story("Task 30 - Create user.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Create user with unavailable zip code test")
    @Description("Scenario #3: The test verifies ability to add user to the application with all fields populated and incorrect (unavailable) zip code.")
    @Test
    public void addUserWithIncorrectZipCodeTest() {
        ZipCodeDTO usedZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        UserDTO newUser = userClientBO.getUniqueUser(httpClient, usedZipCode, HttpStatus.SC_OK);

        int expectedResponseCode = HttpStatus.SC_FAILED_DEPENDENCY;
        Response response = userClient.createUser(httpClient, newUser, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsersList = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Create user with unavailable zip code test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals("Specified zip code is not available", actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsersList.contains(newUser), "User has been added to the application but should not."));
    }

    @Issues({
            @Issue("Response code is not valid: 201 instead of 400."),
            @Issue("Response error message is not correct."),
            @Issue("Existing user was added to the application.")
    })
    @Story("Task 30 - Create user.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Create duplicate of already existing user test")
    @Description("Scenario #4: The test verifies ability to add already existing user to the application.")
    @Test
    public void addAlreadyExistingUserTest() {
        UserDTO newUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, newUser, HttpStatus.SC_CREATED);

        int expectedResponseCode = HttpStatus.SC_BAD_REQUEST;
        Response response = userClient.createUser(httpClient, newUser, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsersList = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Create duplicate of already existing user test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals("Users must be unique for complex key name+sex", actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsersList.contains(newUser), "User has been added to the application but should not."));
    }

    @Story("Task 30 - Create user.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Create user with missed required field(s) test")
    @Description("Missed Scenario: The test verifies the impossibility to create user with missed required field(s).")
    @ParameterizedTest
    @MethodSource("missedRequiredFieldsUser")
    public void addUserWithoutRequiredFieldsTest(UserDTO userToAdd) {
        int expectedResponseCode = HttpStatus.SC_CONFLICT;
        Response response = userClient.createUser(httpClient, userToAdd, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsersList = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Create user with missed required field(s) test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals("Some required fields are missed", actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsersList.contains(userToAdd), "User has been added to the application but should not."));
    }
}