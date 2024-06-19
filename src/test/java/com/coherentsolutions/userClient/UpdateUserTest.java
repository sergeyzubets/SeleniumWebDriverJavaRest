package com.coherentsolutions.userClient;

import com.coherentsolutions.BaseTest;
import com.coherentsolutions.data.dto.UpdateUserDTO;
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
import static com.coherentsolutions.data.ErrorMessages.PredefinedErrorMessages.*;
import static com.coherentsolutions.data.ErrorMessages.UserClient.*;
import static com.coherentsolutions.utils.GeneralUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("REST API test.")
@Feature("User Controller test")
public class UpdateUserTest extends BaseTest {

    @Story("Task 50 - Update user.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("PUT a user test")
    @Description("Scenario #1: The test verifies ability to update a user with PUT request.")
    @Test
    public void putUserTest() {
        ZipCodeDTO newZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        zipCodeClient.createZipCodes(httpClient, List.of(newZipCode), HttpStatus.SC_CREATED);
        UserDTO userToChange = userClientBO.getUniqueUser(httpClient, newZipCode, HttpStatus.SC_OK);
        userClient.createUser(httpClient, userToChange, HttpStatus.SC_CREATED);

        UserDTO userNewValues = new UserDTO(userToChange.getName() + " PUT", getRandomGender());
        UpdateUserDTO updateUser = new UpdateUserDTO(userNewValues, userToChange);

        int expectedResponseCode = HttpStatus.SC_OK;
        Response response = userClient.putUser(httpClient, updateUser, expectedResponseCode);

        List<UserDTO> actualUsers = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();
        UserDTO actualUser = actualUsers
                .stream()
                .filter(user -> user.equals(userNewValues))
                .findFirst()
                .orElse(new UserDTO());

        assertAll("PUT a user test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualUsers.isEmpty(), USERS_LIST_SIZE_FAILURE),
                () -> assertEquals(userNewValues.getName(), actualUser.getName(), USERNAME_FAILURE),
                () -> assertEquals(userNewValues.getGender(), actualUser.getGender(), GENDER_FAILURE),
                () -> assertNull(actualUser.getAge(), AGE_FAILURE),
                () -> assertNull(actualUser.getZipCode(), ZIP_CODE_FAILURE)
        );
    }

    @Issue("The patched user has all fields overwritten.")
    @Story("Task 50 - Update user.")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("PATCH a user test")
    @Description("Scenario #1: The test verifies ability to update a user with PATCH request.")
    @Test
    public void patchUserTest() {
        ZipCodeDTO newZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        zipCodeClient.createZipCodes(httpClient, List.of(newZipCode), HttpStatus.SC_CREATED);
        UserDTO userToChange = userClientBO.getUniqueUser(httpClient, newZipCode, HttpStatus.SC_OK);
        userClient.createUser(httpClient, userToChange, HttpStatus.SC_CREATED);

        UserDTO userNewValues = new UserDTO(userToChange.getName() + " PATCH", getRandomGender());
        userNewValues.setAge(getRandomAge());
        UpdateUserDTO updateUser = new UpdateUserDTO(userNewValues, userToChange);
        String requestBody = removeLineFromBody(updateUser);

        int expectedResponseCode = HttpStatus.SC_OK;
        Response response = userClient.patchUser(httpClient, requestBody, expectedResponseCode);

        List<UserDTO> actualUsers = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();
        UserDTO actualUser = actualUsers
                .stream()
                .filter(user -> user.equals(userNewValues))
                .findFirst()
                .orElse(new UserDTO());

        assertAll("PATCH a user test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(userNewValues.getName(), actualUser.getName(), USERNAME_FAILURE),
                () -> assertEquals(userNewValues.getGender(), actualUser.getGender(), GENDER_FAILURE),
                () -> assertEquals(userNewValues.getAge(), actualUser.getAge(), AGE_FAILURE),
                () -> assertEquals(userToChange.getZipCode(), actualUser.getZipCode(), ZIP_CODE_FAILURE)
        );
    }


    @Issue("The updated user was removed from the application.")
    @Story("Task 50 - Update user.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("PUT a user with incorrect zip code test")
    @Description("Scenario #2: The test verifies impossibility to update user with incorrect (unavailable) zip code with PUT request.")
    @Test
    public void putUserWithIncorrectZipCodeTest() {
        UserDTO userToChange = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, userToChange, HttpStatus.SC_CREATED);

        String newName = userToChange.getName() + " PUT";
        ZipCodeDTO unavailableZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        UserDTO userNewValues = new UserDTO(newName, getRandomGender(), getRandomAge(), unavailableZipCode);
        UpdateUserDTO updateUser = new UpdateUserDTO(userNewValues, userToChange);

        int expectedResponseCode = HttpStatus.SC_FAILED_DEPENDENCY;
        Response response = userClient.putUser(httpClient, updateUser, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsers = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();

        assertAll("PUT a user with unavailable zip code test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(USER_WITH_UNAVAILABLE_ZIP_CODE, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsers.contains(userNewValues), UPDATE_USER_FAILURE),
                () -> assertTrue(actualUsers.contains(userToChange), USERS_LIST_FAILURE)
        );
    }

    @Issue("The patched user was removed from the application.")
    @Story("Task 50 - Update user.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("PATCH a user with incorrect zip code test")
    @Description("Scenario #2: The test verifies impossibility to update user with incorrect (unavailable) zip code with PATCH request.")
    @Test
    public void patchUserWithIncorrectZipCodeTest() {
        UserDTO userToChange = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, userToChange, HttpStatus.SC_CREATED);

        String newName = userToChange.getName() + " PATCH";
        ZipCodeDTO unavailableZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        UserDTO userNewValues = new UserDTO(newName, getRandomGender(), getRandomAge(), unavailableZipCode);
        UpdateUserDTO updateUser = new UpdateUserDTO(userNewValues, userToChange);

        int expectedResponseCode = HttpStatus.SC_FAILED_DEPENDENCY;
        Response response = userClient.patchUser(httpClient, updateUser, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsers = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();

        assertAll("PATCH a user with unavailable zip code test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(USER_WITH_UNAVAILABLE_ZIP_CODE, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsers.contains(userNewValues), UPDATE_USER_FAILURE),
                () -> assertTrue(actualUsers.contains(userToChange), USERS_LIST_FAILURE)
        );
    }

    @Issue("The updated user was removed from the application.")
    @Story("Task 50 - Update user.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("PUT user with missed required field(s) test")
    @Description("Scenario #3:The test verifies impossibility to update user with missed required field(s) with PUT request.")
    @ParameterizedTest
    @MethodSource("missedRequiredFieldsUser")
    public void putUserWithoutRequiredFieldsTest(UserDTO userNewValues) {
        UserDTO userToChange = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, userToChange, HttpStatus.SC_CREATED);
        UpdateUserDTO updateUser = new UpdateUserDTO(userNewValues, userToChange);

        int expectedResponseCode = HttpStatus.SC_CONFLICT;
        Response response = userClient.putUser(httpClient, updateUser, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsers = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();

        assertAll("PUT user with missed required field(s) test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(REQUIRED_FIELDS_VALIDATION, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsers.contains(userNewValues), UPDATE_USER_FAILURE),
                () -> assertTrue(actualUsers.contains(userToChange), USERS_LIST_FAILURE)
        );
    }

    @Issue("The patched user was removed from the application.")
    @Story("Task 50 - Update user.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("PATCH user with missed required field(s) test")
    @Description("Scenario #3:The test verifies impossibility to update user with missed required field(s) with PATCH request.")
    @ParameterizedTest
    @MethodSource("missedRequiredFieldsUser")
    public void patchUserWithoutRequiredFieldsTest(UserDTO userNewValues) {
        UserDTO userToChange = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, userToChange, HttpStatus.SC_CREATED);
        UpdateUserDTO updateUser = new UpdateUserDTO(userNewValues, userToChange);

        int expectedResponseCode = HttpStatus.SC_CONFLICT;
        Response response = userClient.patchUser(httpClient, updateUser, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsers = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();

        assertAll("PATCH user with missed required field(s) test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(REQUIRED_FIELDS_VALIDATION, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsers.contains(userNewValues), UPDATE_USER_FAILURE),
                () -> assertTrue(actualUsers.contains(userToChange), USERS_LIST_FAILURE)
        );
    }

    @Story("Task 50 - Update user.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("PUT non-existent user test")
    @Description("Missed Scenario: The test verifies the impossibility to update non-existent user with PUT request.")
    @Test
    public void putNonExistentUserTest() {
        UserDTO userToChange = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        UserDTO userNewValues = new UserDTO(userToChange.getName() + " PUT", getRandomGender());
        UpdateUserDTO updateUser = new UpdateUserDTO(userNewValues, userToChange);

        int expectedResponseCode = HttpStatus.SC_BAD_REQUEST;
        Response response = userClient.putUser(httpClient, updateUser, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsers = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();

        assertAll("PUT non-existent user test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(USER_UPDATE_ERROR, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsers.contains(userNewValues), UPDATE_USER_FAILURE),
                () -> assertFalse(actualUsers.contains(userToChange), UPDATE_USER_FAILURE)
        );
    }

    @Story("Task 50 - Update user.")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("PATCH non-existent user test")
    @Description("Missed Scenario: The test verifies the impossibility to update non-existent user with PATCH request.")
    @Test
    public void patchNonExistentUserTest() {
        UserDTO userToChange = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        UserDTO userNewValues = new UserDTO(userToChange.getName() + " PATCH", getRandomGender());
        UpdateUserDTO updateUser = new UpdateUserDTO(userNewValues, userToChange);

        int expectedResponseCode = HttpStatus.SC_BAD_REQUEST;
        Response response = userClient.patchUser(httpClient, updateUser, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> actualUsers = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();

        assertAll("PATCH non-existent user test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(USER_UPDATE_ERROR, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertFalse(actualUsers.contains(userNewValues), UPDATE_USER_FAILURE),
                () -> assertFalse(actualUsers.contains(userToChange), UPDATE_USER_FAILURE)
        );
    }
}