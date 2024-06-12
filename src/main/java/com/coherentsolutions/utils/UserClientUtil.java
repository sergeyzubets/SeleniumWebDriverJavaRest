package com.coherentsolutions.utils;

import com.coherentsolutions.clients.UserClient;
import com.coherentsolutions.dto.UserDTO;
import com.coherentsolutions.dto.ZipCodeDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.util.List;
import java.util.Random;

@Slf4j
public class UserClientUtil extends GeneralUtil {

    public static String getRandomUserName() {
        return FAKER.name().fullName();
    }

    public static Gender getRandomGender() {
        int i = new Random().nextInt(2);
        return i == 1 ? Gender.MALE : Gender.FEMALE;
    }

    public static Integer getRandomAge() {
        int maxUserAge = 120;
        return new Random().nextInt(maxUserAge);
    }

    public static String convertUserToJsonBody(UserDTO user) {
        String requestBody = null;
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            requestBody = objectWriter.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return requestBody;
    }

    @Step("Getting unique user with all fields populated.")
    public static UserDTO getUniqueUser(CloseableHttpClient httpClient, ZipCodeDTO zipCode) {
        List<UserDTO> availableUsers = (List<UserDTO>) new UserClient().getUsers(httpClient).getParsedBody();

        long whileStartTime = System.currentTimeMillis();
        UserDTO uniqueUser;

        while (true) {
            uniqueUser = new UserDTO(getRandomUserName(), getRandomGender(), getRandomAge(), zipCode);
            if (!availableUsers.contains(uniqueUser)) {
                break;
            }

            if (System.currentTimeMillis() >= whileStartTime + WHILE_LIFETIME_SEC * 1000) {
                log.warn(WHILE_INTERRUPTION_MESSAGE);
                break;
            }
        }
        return uniqueUser;
    }
}