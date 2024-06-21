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

import static com.coherentsolutions.data.ErrorMessages.Common.ERROR_MESSAGE_FAILURE;
import static com.coherentsolutions.data.ErrorMessages.Common.RESPONSE_CODE_FAILURE;
import static com.coherentsolutions.data.ErrorMessages.PredefinedErrorMessages.REQUIRED_FIELDS_VALIDATION;
import static com.coherentsolutions.data.ErrorMessages.PredefinedErrorMessages.USED_NOT_FOUND;
import static com.coherentsolutions.data.ErrorMessages.UserClient.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("REST API test")
@Feature("User Controller test")
public class DeleteUserTest extends BaseTest {

    @Story("Task 60 - Delete user")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Delete a user with all fields populated test")
    @Description("Scenario #1: The test verifies ability to delete a user with all fields populated from the application. " +
            "Also, the test verifies returning of used zip code to available list.")
    @Test
    public void deleteUserAllFieldsPopulatedTest() {
        ZipCodeDTO usedZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        zipCodeClient.createZipCodes(httpClient, List.of(usedZipCode), HttpStatus.SC_CREATED);
        UserDTO userToDelete = userClientBO.getUniqueUser(httpClient, usedZipCode, HttpStatus.SC_OK);
        userClient.createUser(httpClient, userToDelete, HttpStatus.SC_CREATED);

        int expectedResponseCode = HttpStatus.SC_NO_CONTENT;
        Response response = userClient.deleteUser(httpClient, userToDelete, expectedResponseCode);
        List<ZipCodeDTO> actualZipCodes = zipCodeClientBO.getAvailableZipCodes(httpClient, HttpStatus.SC_OK);
        List<UserDTO> actualUsers = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Delete a user with all fields populated test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualUsers.contains(userToDelete), DELETE_USER_FAILURE),
                () -> assertTrue(actualZipCodes.contains(usedZipCode), RETURN_ZIP_CODE_FAILURE)
        );
    }

    @Issues({
            @Issue("The user has not been deleted from the application."),
            @Issue("Used zip code has not been returned to list of available codes.")
    })
    @Story("Task 60 - Delete user")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Delete a user with only required fields populated test")
    @Description("Scenario #2: The test verifies ability to delete a user with only required fields populated from the application.")
    @Test
    public void deleteUserWithRequiredFieldsPopulatedTest() {
        ZipCodeDTO usedZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        zipCodeClient.createZipCodes(httpClient, List.of(usedZipCode), HttpStatus.SC_CREATED);
        UserDTO newUser = userClientBO.getUniqueUser(httpClient, usedZipCode, HttpStatus.SC_OK);
        userClient.createUser(httpClient, newUser, HttpStatus.SC_CREATED);

        UserDTO userToDelete = new UserDTO(newUser.getName(), newUser.getGender());

        int expectedResponseCode = HttpStatus.SC_NO_CONTENT;
        Response response = userClient.deleteUser(httpClient, userToDelete, expectedResponseCode);
        List<ZipCodeDTO> actualZipCodes = zipCodeClientBO.getAvailableZipCodes(httpClient, HttpStatus.SC_OK);
        List<UserDTO> actualUsers = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Delete a user with only required fields populated test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualUsers.contains(userToDelete), DELETE_USER_FAILURE),
                () -> assertTrue(actualZipCodes.contains(usedZipCode), RETURN_ZIP_CODE_FAILURE)
        );
    }

    @Story("Task 60 - Delete user")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Delete user with missed required field(s) test")
    @Description("Scenario #3: The test verifies impossibility to delete a user with missed required field(s).")
    @ParameterizedTest
    @MethodSource("missedRequiredFieldsUser")
    public void deleteUserWithoutRequiredFieldsTest(UserDTO userToDelete) {
        UserDTO newUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, newUser, HttpStatus.SC_CREATED);

        int expectedResponseCode = HttpStatus.SC_CONFLICT;
        Response response = userClient.deleteUser(httpClient, userToDelete, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsers = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Delete user with missed required field(s) test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(REQUIRED_FIELDS_VALIDATION, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertTrue(actualUsers.contains(newUser), USERS_LIST_FAILURE)
        );
    }

    @Issues({
            @Issue("Response code is not valid: 204 instead of 404."),
            @Issue("Response error message is not correct."),
    })
    @Story("Task 60 - Delete user")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Delete non-existent user test")
    @Description("Missed Scenario: The test verifies the impossibility to delete non-existent user.")
    @Test
    public void deleteNonExistentUserTest() {
        UserDTO newUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, newUser, HttpStatus.SC_CREATED);
        UserDTO nonExistentUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);

        int expectedResponseCode = HttpStatus.SC_NOT_FOUND;
        Response response = userClient.deleteUser(httpClient, nonExistentUser, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsers = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Delete non-existent user test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(USED_NOT_FOUND, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsers.contains(nonExistentUser), USERS_LIST_FAILURE)
        );
    }
}