package com.coherentsolutions.clients;

import com.coherentsolutions.data.dto.UserDTO;
import com.coherentsolutions.data.models.FailedResponseBody;
import com.coherentsolutions.data.models.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.net.URI;
import java.util.List;

import static com.coherentsolutions.utils.GeneralUtil.convertUserToJsonBody;
import static com.coherentsolutions.utils.GeneralUtil.logRequest;

@Slf4j
public class UserClient extends BaseClient {

    public Response createUser(CloseableHttpClient httpClient, UserDTO userToAdd, int code) {
        String path = "/users";
        URI uri = getUri(path);

        String requestBody = convertUserToJsonBody(userToAdd);
        HttpEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);

        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Authorization", HttpClient.getWriteToken());
        httpPost.addHeader("accept", "*/*");
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(stringEntity);

        String requestName = "Create a user request";
        logRequest(requestName, httpPost, requestBody);

        Response response = sendRequest(httpClient, httpPost, requestName, code);
        FailedResponseBody body = new FailedResponseBody();

        try {
            if(!response.getBody().isEmpty()) {
                body = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {
                });
            }
            return new Response(response.getCode(), body);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Response getUsers(CloseableHttpClient httpClient, int code) {
        String path = "/users";
        URI uri =getUri(path);

        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Authorization", HttpClient.getReadToken());
        httpGet.addHeader("accept", "application/json");

        String requestName = "Get all users";
        logRequest(requestName, httpGet);
        Response response = sendRequest(httpClient, httpGet, requestName, code);

        try {
            List<UserDTO> users = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {
            });
            return new Response(response.getCode(), users);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}