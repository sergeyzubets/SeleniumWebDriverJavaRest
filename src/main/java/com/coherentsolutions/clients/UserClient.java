package com.coherentsolutions.clients;

import com.coherentsolutions.data.dto.UpdateUserDTO;
import com.coherentsolutions.data.dto.UserDTO;
import com.coherentsolutions.data.models.FailedResponseBody;
import com.coherentsolutions.data.models.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.coherentsolutions.utils.GeneralUtil.*;

@Slf4j
public class UserClient extends BaseClient {
    private static final String PATH = "/users";

    public Response getUsers(CloseableHttpClient httpClient, int code) {
        return proceedGetRequest(httpClient, code, new HttpGet(getUri(PATH)));
    }

    public Response getUsers(CloseableHttpClient httpClient, int code, Map<String, String> paramMap) {
        return proceedGetRequest(httpClient, code, new HttpGet(getUri(PATH, paramMap)));
    }

    public Response createUser(CloseableHttpClient httpClient, UserDTO userToAdd, int code) {
        String requestBody = convertObjectToJson(userToAdd);
        return proceedCUDRequest(httpClient, requestBody, code, new HttpPost(getUri(PATH)), "Create a user");
    }

    public Response putUser(CloseableHttpClient httpClient, UpdateUserDTO updateUserDTO, int code) {
        String requestBody = convertObjectToJson(updateUserDTO);
        return proceedCUDRequest(httpClient, requestBody, code, new HttpPut(getUri(PATH)), "PUT a user");
    }

    public Response patchUser(CloseableHttpClient httpClient, String requestBody, int code) {
        return proceedCUDRequest(httpClient, requestBody, code, new HttpPatch(getUri(PATH)), "PATCH a user");
    }

    public Response patchUser(CloseableHttpClient httpClient, UpdateUserDTO updateUserDTO, int code) {
        String requestBody = convertObjectToJson(updateUserDTO);
        return proceedCUDRequest(httpClient, requestBody, code, new HttpPatch(getUri(PATH)), "PATCH a user");
    }

    public Response deleteUser(CloseableHttpClient httpClient, UserDTO userToAdd, int code) {
        String requestBody = convertObjectToJson(userToAdd);
        return proceedCUDRequest(httpClient, requestBody, code, new HttpDelete(getUri(PATH)), "DELETE a user");
    }

    public Response uploadUser(CloseableHttpClient httpClient, File file, int code) {
        String path = "/users/upload";
        return proceedUploadRequest(httpClient, file, code, new HttpPost(getUri(path)));
    }

    private Response proceedGetRequest(CloseableHttpClient httpClient, int code, HttpGet httpGet) {
        String name = "Get all users";
        configureRequest(name, httpGet);
        Response response = sendRequest(httpClient, httpGet, name, code);
        FailedResponseBody body;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if (root.isArray()) {
                List<UserDTO> users = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {
                });
                return new Response(response.getCode(), users);
            } else {
                body = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {
                });
                return new Response(response.getCode(), body);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Response proceedCUDRequest(CloseableHttpClient httpClient, String requestBody, int code, HttpUriRequestBase request, String name) {
        configureRequest(name, request, requestBody);
        Response response = sendRequest(httpClient, request, name, code);
        FailedResponseBody body = new FailedResponseBody();

        try {
            if (response.getBody() != null && !response.getBody().isEmpty()) {
                body = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {
                });
            }
            return new Response(response.getCode(), body);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Response proceedUploadRequest(CloseableHttpClient httpClient, File file, int code, HttpPost httpPost) {
        String name = "Upload a user";
        configureRequest(name, httpPost, file);
        Response response = sendRequest(httpClient, httpPost, name, code);
        FailedResponseBody body;

        try {
            if (response.getCode() == HttpStatus.SC_CREATED) {
                return new Response(response.getCode(), response.getBody());
            } else {
                body = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {
                });
            }
            return new Response(response.getCode(), body);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void configureRequest(String name, HttpGet httpGet) {
        httpGet.addHeader("Authorization", HttpClient.getReadToken());
        httpGet.addHeader("accept", "application/json");
        logRequest(name, httpGet);
    }

    private void configureRequest(String name, HttpUriRequestBase request, String requestBody) {
        HttpEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        request.addHeader("Authorization", HttpClient.getWriteToken());
        request.addHeader("accept", "*/*");
        request.addHeader("Content-Type", "application/json");
        request.setEntity(stringEntity);
        logRequest(name, request, requestBody);
    }

    private void configureRequest(String name, HttpPost httpPost, File file) {
        httpPost.addHeader("Authorization", HttpClient.getWriteToken());
        httpPost.addHeader("accept", "*/*");
        HttpEntity entity = convertFileToEntity(file);
        httpPost.setEntity(entity);
        String requestBody = convertEntityToString(entity);
        HttpEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        logRequest(name, httpPost, stringEntity, requestBody);
    }
}