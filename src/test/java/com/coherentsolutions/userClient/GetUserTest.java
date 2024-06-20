package com.coherentsolutions.userClient;

import com.coherentsolutions.BaseTest;
import com.coherentsolutions.data.Gender;
import com.coherentsolutions.data.GetUserParameters;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.coherentsolutions.data.ErrorMessages.Common.ERROR_MESSAGE_FAILURE;
import static com.coherentsolutions.data.ErrorMessages.Common.RESPONSE_CODE_FAILURE;
import static com.coherentsolutions.data.ErrorMessages.PredefinedErrorMessages.CONFLICT_PARAMETERS;
import static com.coherentsolutions.data.ErrorMessages.UserClient.*;
import static com.coherentsolutions.utils.GeneralUtil.getRandomAge;
import static org.junit.jupiter.api.Assertions.*;

@Epic("REST API test")
@Feature("User Controller test")
public class GetUserTest extends BaseTest {

    @Story("Task 40 - Get users and filter them")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Get all users stored in the application test")
    @Description("Scenario #1: The test verifies ability to get all stored in the application users.")
    @Test
    public void getAllUsersTest() {
        ZipCodeDTO usedZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        zipCodeClient.createZipCodes(httpClient, List.of(usedZipCode), HttpStatus.SC_CREATED);
        UserDTO newUser = userClientBO.getUniqueUser(httpClient, usedZipCode, HttpStatus.SC_OK);
        userClient.createUser(httpClient, newUser, HttpStatus.SC_CREATED);

        int expectedResponseCode = HttpStatus.SC_OK;
        Response response = userClient.getUsers(httpClient, expectedResponseCode);
        List<UserDTO> actualUsers = (List<UserDTO>) response.getParsedBody();

        UserDTO actualUser = actualUsers
                .stream()
                .filter(user -> user.equals(newUser))
                .findFirst()
                .orElse(new UserDTO());

        assertAll("Get all users stored in the application for now test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualUsers.isEmpty(), USERS_LIST_SIZE_FAILURE),
                () -> assertEquals(newUser.getName(), actualUser.getName(), USERNAME_FAILURE),
                () -> assertEquals(newUser.getGender(), actualUser.getGender(), GENDER_FAILURE),
                () -> assertEquals(newUser.getAge(), actualUser.getAge(), AGE_FAILURE),
                () -> assertEquals(newUser.getZipCode(), actualUser.getZipCode(), ZIP_CODE_FAILURE)
        );
    }

    @Story("Task 40 - Get users and filter them")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Get all users older than value test")
    @Description("Scenario #2: The test verifies ability to get all users older than value of parameter.")
    @Test
    public void getOlderThanUsersTest() {
        int targetAge = getRandomAge();
        UserDTO youngerThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        youngerThanUser.setAge(targetAge);
        userClient.createUser(httpClient, youngerThanUser, HttpStatus.SC_CREATED);

        UserDTO olderThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        olderThanUser.setAge(targetAge + 1);
        userClient.createUser(httpClient, olderThanUser, HttpStatus.SC_CREATED);

        List<UserDTO> usersList = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();
        List<UserDTO> expectedUsers = usersList
                .stream()
                .filter(user -> user.getAge() != null && user.getAge() > targetAge)
                .collect(Collectors.toList());

        int expectedResponseCode = HttpStatus.SC_OK;
        Map<String, String> paramMap = Map.of(GetUserParameters.OLDER_THAN.getValue(), String.valueOf(targetAge));
        Response response = userClient.getUsers(httpClient, expectedResponseCode, paramMap);
        List<UserDTO> actualUsers = (List<UserDTO>) response.getParsedBody();

        assertAll("Get all users older than value test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualUsers.isEmpty(), USERS_LIST_SIZE_FAILURE),
                () -> expectedUsers.forEach(user -> assertTrue(actualUsers.contains(user), PARAMETRIZED_LIST_FAILURE + user)),
                () -> actualUsers.forEach(user -> assertTrue(expectedUsers.contains(user), PARAMETRIZED_LIST_EXTRA_USER_FAILURE + user))
        );
    }

    @Story("Task 40 - Get users and filter them")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Get all users younger than value test")
    @Description("Scenario #3: The test verifies ability to get all users younger than value of parameter.")
    @Test
    public void getYoungerThanUsersTest() {
        int targetAge = getRandomAge();
        UserDTO youngerThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        youngerThanUser.setAge(targetAge - 1);
        userClient.createUser(httpClient, youngerThanUser, HttpStatus.SC_CREATED);

        UserDTO olderThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        olderThanUser.setAge(targetAge);
        userClient.createUser(httpClient, olderThanUser, HttpStatus.SC_CREATED);

        List<UserDTO> usersList = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();
        List<UserDTO> expectedUsers = usersList
                .stream()
                .filter(user -> user.getAge() != null && user.getAge() < targetAge)
                .collect(Collectors.toList());

        int expectedResponseCode = HttpStatus.SC_OK;
        Map<String, String> paramMap = Map.of(GetUserParameters.YOUNGER_THAN.getValue(), String.valueOf(targetAge));
        Response response = userClient.getUsers(httpClient, expectedResponseCode, paramMap);
        List<UserDTO> actualUsers = (List<UserDTO>) response.getParsedBody();

        assertAll("Get all users younger than value test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualUsers.isEmpty(), USERS_LIST_SIZE_FAILURE),
                () -> expectedUsers.forEach(user -> assertTrue(actualUsers.contains(user), PARAMETRIZED_LIST_FAILURE + user)),
                () -> actualUsers.forEach(user -> assertTrue(expectedUsers.contains(user), PARAMETRIZED_LIST_EXTRA_USER_FAILURE + user))
        );
    }

    @Story("Task 40 - Get users and filter them")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Get all users with specified gender test")
    @Description("Scenario #4: The test verifies ability to get all users with gender value of parameter.")
    @Test
    public void getUsersByGenderTest() {
        UserDTO user1 = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, user1, HttpStatus.SC_CREATED);

        UserDTO user2 = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, user2, HttpStatus.SC_CREATED);
        Gender targetGender = user2.getGender();

                List<UserDTO> usersList = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();
        List<UserDTO> expectedUsers = usersList
                .stream()
                .filter(user -> user.getGender() != null && user.getGender().equals(targetGender))
                .collect(Collectors.toList());

        int expectedResponseCode = HttpStatus.SC_OK;
        Map<String, String> paramMap = Map.of(GetUserParameters.SEX.getValue(), targetGender.getValue());
        Response response = userClient.getUsers(httpClient, expectedResponseCode, paramMap);
        List<UserDTO> actualUsers = (List<UserDTO>) response.getParsedBody();

        assertAll("Get all users with specified gender test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualUsers.isEmpty(), USERS_LIST_SIZE_FAILURE),
                () -> expectedUsers.forEach(user -> assertTrue(actualUsers.contains(user), PARAMETRIZED_LIST_FAILURE + user)),
                () -> actualUsers.forEach(user -> assertTrue(expectedUsers.contains(user), PARAMETRIZED_LIST_EXTRA_USER_FAILURE + user))
        );
    }

    @Story("Task 40 - Get users and filter them")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Get all users with specified gender and younger than value test")
    @Description("Missed Scenario #1: The test verifies ability to get all users with gender value of parameter and younger than value.")
    @Test
    public void getUsersByGenderAndYoungerThanTest() {
        int targetAge = getRandomAge();
        UserDTO olderThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        olderThanUser.setAge(targetAge);
        userClient.createUser(httpClient, olderThanUser, HttpStatus.SC_CREATED);

        UserDTO youngerThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        youngerThanUser.setAge(targetAge - 1);
        userClient.createUser(httpClient, youngerThanUser, HttpStatus.SC_CREATED);
        Gender targetGender = youngerThanUser.getGender();

        List<UserDTO> usersList = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();
        List<UserDTO> expectedUsers = usersList
                .stream()
                .filter(user -> user.getGender() != null && user.getGender().equals(targetGender)
                        && user.getAge() != null && user.getAge() < targetAge)
                .collect(Collectors.toList());

        int expectedResponseCode = HttpStatus.SC_OK;
        Map<String, String> paramMap = Map.of(
                GetUserParameters.SEX.getValue(), targetGender.getValue(),
                GetUserParameters.YOUNGER_THAN.getValue(), String.valueOf(targetAge)
        );

        Response response = userClient.getUsers(httpClient, expectedResponseCode, paramMap);
        List<UserDTO> actualUsers = (List<UserDTO>) response.getParsedBody();

        assertAll("Get all users with specified gender and younger than value test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualUsers.isEmpty(), USERS_LIST_SIZE_FAILURE),
                () -> expectedUsers.forEach(user -> assertTrue(actualUsers.contains(user), PARAMETRIZED_LIST_FAILURE + user)),
                () -> actualUsers.forEach(user -> assertTrue(expectedUsers.contains(user), PARAMETRIZED_LIST_EXTRA_USER_FAILURE + user))
        );
    }

    @Story("Task 40 - Get users and filter them")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Get all users with specified gender and older than value test")
    @Description("Missed Scenario #2: The test verifies ability to get all users with gender value of parameter and older than value.")
    @Test
    public void getUsersByGenderAndOlderThanTest() {
        int targetAge = getRandomAge();
        UserDTO olderThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        olderThanUser.setAge(targetAge + 1);
        userClient.createUser(httpClient, olderThanUser, HttpStatus.SC_CREATED);
        Gender targetGender = olderThanUser.getGender();

        UserDTO youngerThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        youngerThanUser.setAge(targetAge);
        userClient.createUser(httpClient, youngerThanUser, HttpStatus.SC_CREATED);

        List<UserDTO> usersList = (List<UserDTO>) userClient.getUsers(httpClient, HttpStatus.SC_OK).getParsedBody();
        List<UserDTO> expectedUsers = usersList
                .stream()
                .filter(user -> user.getGender() != null && user.getGender().equals(targetGender)
                        && user.getAge() != null && user.getAge() > targetAge)
                .collect(Collectors.toList());

        int expectedResponseCode = HttpStatus.SC_OK;
        Map<String, String> paramMap = Map.of(
                GetUserParameters.SEX.getValue(), targetGender.getValue(),
                GetUserParameters.OLDER_THAN.getValue(), String.valueOf(targetAge)
        );

        Response response = userClient.getUsers(httpClient, expectedResponseCode, paramMap);
        List<UserDTO> actualUsers = (List<UserDTO>) response.getParsedBody();

        assertAll("Get all users with specified gender and older than value test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertFalse(actualUsers.isEmpty(), USERS_LIST_SIZE_FAILURE),
                () -> expectedUsers.forEach(user -> assertTrue(actualUsers.contains(user), PARAMETRIZED_LIST_FAILURE + user)),
                () -> actualUsers.forEach(user -> assertTrue(expectedUsers.contains(user), PARAMETRIZED_LIST_EXTRA_USER_FAILURE + user))
        );
    }

    @Story("Task 40 - Get users and filter them")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Get all users older and younger than value test")
    @Description("Missed Scenario #3: The test verifies impossibility of using parameters youngerThan and olderThan together.")
    @Test
    public void getUsersConflictParamsTest() {
        int targetAge = getRandomAge();
        UserDTO olderThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        olderThanUser.setAge(targetAge + 1);
        userClient.createUser(httpClient, olderThanUser, HttpStatus.SC_CREATED);

        UserDTO youngerThanUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        youngerThanUser.setAge(targetAge - 1);
        userClient.createUser(httpClient, youngerThanUser, HttpStatus.SC_CREATED);

        int expectedResponseCode = HttpStatus.SC_CONFLICT;
        Map<String, String> paramMap = Map.of(
                GetUserParameters.OLDER_THAN.getValue(), String.valueOf(targetAge),
                GetUserParameters.YOUNGER_THAN.getValue(), String.valueOf(targetAge)
        );

        Response response = userClient.getUsers(httpClient, expectedResponseCode, paramMap);
        FailedResponseBody actualBody = response.getFailedBody();

        assertAll("Get all users older and younger than value test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(CONFLICT_PARAMETERS, actualBody.getMessage(), ERROR_MESSAGE_FAILURE)
        );
    }
}