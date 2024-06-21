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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.coherentsolutions.data.ErrorMessages.Common.ERROR_MESSAGE_FAILURE;
import static com.coherentsolutions.data.ErrorMessages.Common.RESPONSE_CODE_FAILURE;
import static com.coherentsolutions.data.ErrorMessages.PredefinedErrorMessages.*;
import static com.coherentsolutions.data.ErrorMessages.UserClient.*;
import static com.coherentsolutions.utils.GeneralUtil.getUploadedUsersAmount;
import static com.coherentsolutions.utils.GeneralUtil.writeUsersToJsonFile;
import static org.junit.jupiter.api.Assertions.*;

@Epic("REST API test")
@Feature("User Controller test")
public class UploadUserTest extends BaseTest {

    @Story("Task 70 - Upload user")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @DisplayName("Upload users to the application test")
    @Description("Scenario #1: The test verifies ability to upload users to the application and replace already existing.")
    @Test
    public void uploadUsersTest() {
        ZipCodeDTO usedZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        zipCodeClient.createZipCodes(httpClient, List.of(usedZipCode), HttpStatus.SC_CREATED);
        UserDTO user1 = userClientBO.getUniqueUser(httpClient, usedZipCode, HttpStatus.SC_OK);
        UserDTO user2 = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        UserDTO user3 = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        List<UserDTO> usersToUpload = List.of(user1, user2, user3);

        Path validUsers = Paths.get("target", "validUsers.json");
        File file = new File(String.valueOf(validUsers));
        writeUsersToJsonFile(usersToUpload, file);

        UserDTO existingUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, existingUser, HttpStatus.SC_CREATED);
        int expectedResponseCode = HttpStatus.SC_CREATED;
        Response response = userClient.uploadUser(httpClient, file, expectedResponseCode);
        int uploadedUsersAmount = getUploadedUsersAmount(response.getBody());

        List<ZipCodeDTO> actualZipCodes = zipCodeClientBO.getAvailableZipCodes(httpClient, HttpStatus.SC_OK);
        List<UserDTO> actualUsers = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Upload users to the application test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(usersToUpload.size(), uploadedUsersAmount, UPLOAD_USERS_RESPONSE_FAILURE),
                () -> assertEquals(actualUsers, usersToUpload, UPLOAD_USER_FAILURE),
                () -> assertFalse(actualZipCodes.contains(usedZipCode), USED_ZIP_CODE_FAILURE)
        );
    }

    @Issues({
            @Issue("Response code is not valid: 500 instead of 424."),
            @Issue("Response error message is not correct."),
            @Issue("Existing users were removed from the application.")
    })
    @Story("Task 70 - Upload user")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Upload users with incorrect (unavailable) zip code test")
    @Description("Scenario #2: The test verifies ability to upload users with incorrect (unavailable) zip code to the application.")
    @Test
    public void uploadUsersWithIncorrectZipCodeTest() {
        ZipCodeDTO usedZipCode = zipCodeClientBO.getNonexistentZipCode(httpClient, HttpStatus.SC_OK);
        UserDTO user1 = userClientBO.getUniqueUser(httpClient, usedZipCode, HttpStatus.SC_OK);
        UserDTO user2 = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        UserDTO user3 = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        List<UserDTO> usersToUpload = List.of(user1, user2, user3);

        Path invalidZipCodeUsers = Paths.get("target", "incorrectZipCodeUsers.json");
        File file = new File(String.valueOf(invalidZipCodeUsers));
        writeUsersToJsonFile(usersToUpload, file);

        UserDTO existingUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, existingUser, HttpStatus.SC_CREATED);
        List<UserDTO> usersBeforeUpload = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        int expectedResponseCode = HttpStatus.SC_FAILED_DEPENDENCY;
        Response response = userClient.uploadUser(httpClient, file, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> usersAfterUpload = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Upload users with incorrect (unavailable) zip code test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(USER_WITH_UNAVAILABLE_ZIP_CODE, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertEquals(usersBeforeUpload, usersAfterUpload, UPLOAD_INVALID_USER_FAILURE)
        );
    }

    @Issues({
            @Issue("Response code is not valid: 500 instead of 409."),
            @Issue("Response error message is not correct."),
            @Issue("Existing users were removed from the application.")
    })
    @Story("Task 70 - Upload user")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Upload users with missed required field(s) test")
    @Description("Scenario #3: The test verifies ability to upload users with missed required field(s) to the application.")
    @ParameterizedTest
    @MethodSource("missedRequiredFieldsUser")
    public void uploadUsersWithoutRequiredFieldTest(UserDTO invalidUser) {
        List<UserDTO> usersToUpload = List.of(invalidUser);
        Path invalidUsers = Paths.get("target", "invalidUsers.json");
        File file = new File(String.valueOf(invalidUsers));
        writeUsersToJsonFile(usersToUpload, file);

        UserDTO existingUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, existingUser, HttpStatus.SC_CREATED);
        List<UserDTO> usersBeforeUpload = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        int expectedResponseCode = HttpStatus.SC_CONFLICT;
        Response response = userClient.uploadUser(httpClient, file, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> usersAfterUpload = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Upload users with missed required field(s) test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(REQUIRED_FIELDS_VALIDATION, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertEquals(usersBeforeUpload, usersAfterUpload, UPLOAD_INVALID_USER_FAILURE)
        );
    }

    @Story("Task 70 - Upload user")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Upload users from invalid file test")
    @Description("Missed Scenario: The test verifies the impossibility to upload users from invalid file.")
    @Test
    public void uploadUsersInvalidFileTest() {
        Path invalidFile = Paths.get("src", "main", "resources", "json", "unparsibleFile.json");
        File file = new File(String.valueOf(invalidFile));

        UserDTO existingUser = userClientBO.getUniqueUser(httpClient, HttpStatus.SC_OK);
        userClient.createUser(httpClient, existingUser, HttpStatus.SC_CREATED);
        List<UserDTO> usersBeforeUpload = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        int expectedResponseCode = HttpStatus.SC_BAD_REQUEST;
        Response response = userClient.uploadUser(httpClient, file, expectedResponseCode);
        FailedResponseBody actualBody = response.getFailedBody();
        List<UserDTO> usersAfterUpload = userClientBO.getAvailableUsers(httpClient, HttpStatus.SC_OK);

        assertAll("Upload users from invalid file test failed.",
                () -> assertEquals(expectedResponseCode, response.getCode(), RESPONSE_CODE_FAILURE),
                () -> assertEquals(UPLOAD_INVALID_FILE, actualBody.getMessage(), ERROR_MESSAGE_FAILURE),
                () -> assertEquals(usersBeforeUpload, usersAfterUpload, UPLOAD_INVALID_USER_FAILURE)
        );
    }
}