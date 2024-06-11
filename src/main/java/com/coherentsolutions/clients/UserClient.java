package com.coherentsolutions.clients;

import com.coherentsolutions.dto.UserDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.coherentsolutions.utils.GeneralUtil.logRequest;
import static com.coherentsolutions.utils.GeneralUtil.logResponse;
import static com.coherentsolutions.utils.UserClientUtil.convertUserToJsonBody;

@Slf4j
@Getter
public class UserClient extends BaseClient {
    private CloseableHttpResponse response;
    private List<UserDTO> users;

    public CloseableHttpResponse sendPostCreateUserRequest(CloseableHttpClient httpClient, String writeToken, UserDTO userToAdd) {
        String path = "/users";
        URI uri = getUri(path);

        String requestBody = convertUserToJsonBody(userToAdd);
        HttpEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);

        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Authorization", writeToken);
        httpPost.addHeader("accept", "*/*");
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(stringEntity);

        String requestName = "Create a user request";
        logRequest(requestName, httpPost, requestBody);

        try {
            response = (CloseableHttpResponse) httpClient.execute(httpPost, response1 -> {
                int statusCode = response1.getCode();
                if (statusCode != HttpStatus.SC_CREATED) {
                    logIncorrectResponseCode(HttpStatus.SC_CREATED, statusCode);
                }

                HttpEntity entity = response1.getEntity();
                if (entity == null) {
                    logNullEntity();
                    logResponse(requestName, response1, null);
                } else {
                    String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    logResponse(requestName, response1, responseBody);
                }
                return response1;
            });
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } finally {
            releaseRequestResources(requestName, response, httpPost);
        }
        return response;
    }

    public CloseableHttpResponse sendGetUsersRequest(CloseableHttpClient httpClient, String readToken) {
        String path = "/users";
        URI uri =getUri(path);

        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Authorization", readToken);
        httpGet.addHeader("accept", "application/json");

        String requestName = "Get all users";
        logRequest(requestName, httpGet);

        try {
            response = (CloseableHttpResponse) httpClient.execute(httpGet, response1 -> {
                int statusCode = response1.getCode();
                if (statusCode != HttpStatus.SC_OK) {
                    logIncorrectResponseCode(HttpStatus.SC_OK, statusCode);
                }

                HttpEntity entity = response1.getEntity();
                if (entity == null) {
                    logNullEntity();
                    logResponse(requestName, response1, null);
                } else {
                    String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    logResponse(requestName, response1, responseBody);
                    users = new ObjectMapper().readValue(responseBody, new TypeReference<>() {
                    });
                }
                return response1;
            });
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } finally {
            releaseRequestResources(requestName, response, httpGet);
        }
        return response;
    }
}