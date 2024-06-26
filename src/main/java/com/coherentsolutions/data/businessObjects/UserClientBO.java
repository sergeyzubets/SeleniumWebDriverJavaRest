package com.coherentsolutions.data.businessObjects;

import com.coherentsolutions.data.dto.UserDTO;
import com.coherentsolutions.data.dto.ZipCodeDTO;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.util.List;
import java.util.stream.Collectors;

import static com.coherentsolutions.utils.GeneralUtil.*;

@Slf4j
public class UserClientBO extends BaseBO {

    @Step("Getting unique user with all fields populated.")
    public UserDTO getUniqueUser(CloseableHttpClient httpClient, ZipCodeDTO zipCode, int code) {
        List<UserDTO> availableUsers = getAvailableUsers(httpClient, code);
        UserDTO uniqueUser = new UserDTO();
        boolean running = true;
        long whileStartTime = System.currentTimeMillis();

        while (running) {
            uniqueUser = new UserDTO(getRandomUserName(), getRandomGender(), getRandomAge(), zipCode);
            if (!availableUsers.contains(uniqueUser)) {
                break;
            }
            running = keepWhile(whileStartTime);
        }
        return uniqueUser;
    }

    @Step("Getting unique user with mandatory fields populated.")
    public UserDTO getUniqueUser(CloseableHttpClient httpClient, int code) {
        List<UserDTO> availableUsers = getAvailableUsers(httpClient, code);
        UserDTO uniqueUser = new UserDTO();
        boolean running = true;
        long whileStartTime = System.currentTimeMillis();

        while (running) {
            uniqueUser = new UserDTO(getRandomUserName(), getRandomGender());
            if (!availableUsers.contains(uniqueUser)) {
                break;
            }
            running = keepWhile(whileStartTime);
        }
        return uniqueUser;
    }

    @Step("Getting all available in the application users.")
    public List<UserDTO> getAvailableUsers(CloseableHttpClient httpClient, int code) {
        return (List<UserDTO>) userClient.getUsers(httpClient, code).getParsedBody();
    }

    @Step("Getting created by automation user(s).")
    public List<UserDTO> getCreatedUsers(CloseableHttpClient httpClient, int code) {
        List<UserDTO> allUsers = (List<UserDTO>) userClient.getUsers(httpClient, code).getParsedBody();
        return allUsers
                .stream()
                .filter(user -> user.getName().startsWith(PREFIX))
                .collect(Collectors.toList());
    }
}