package com.coherentsolutions.data.businessObjects;

import com.coherentsolutions.data.dto.UserDTO;
import com.coherentsolutions.data.dto.ZipCodeDTO;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.util.List;

import static com.coherentsolutions.utils.GeneralUtil.*;

@Slf4j
public class UserClientBO extends BaseBO {

    @Step("Getting unique user with all fields populated.")
    public UserDTO getUniqueUser(CloseableHttpClient httpClient, ZipCodeDTO zipCode) {
        List<UserDTO> availableUsers = getAvailableUsers(httpClient);

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

    @Step("Getting all available in the application users.")
    public List<UserDTO> getAvailableUsers(CloseableHttpClient httpClient) {
        return (List<UserDTO>) userClient.getUsers(httpClient).getParsedBody();
    }
}