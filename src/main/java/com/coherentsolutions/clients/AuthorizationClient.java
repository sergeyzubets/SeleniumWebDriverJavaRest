package com.coherentsolutions.clients;

import com.coherentsolutions.dto.AccessTokenDTO;
import com.coherentsolutions.models.Response;
import com.coherentsolutions.utils.Scope;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpStatus;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import static com.coherentsolutions.utils.GeneralUtil.logRequest;
import static com.coherentsolutions.utils.GeneralUtil.logTokenDetails;

@Slf4j
@Getter
public class AuthorizationClient extends BaseClient {

    public String getToken(CloseableHttpClient httpClient, Scope scope) {
        AccessTokenDTO accessTokenDTO = requestAccessToken(httpClient, scope);
        logTokenDetails(accessTokenDTO, scope.getValue());
        return getBearerToken(accessTokenDTO);
    }

    private AccessTokenDTO requestAccessToken(CloseableHttpClient httpClient, Scope scope) {
        String path = "/oauth/token";
        Map<String, String> paramMap = Map.of("grant_type", "client_credentials", "scope", scope.getValue());
        URI uri = getUri(path, paramMap);

        HttpPost httpPost = new HttpPost(uri);
        String requestName = "Get " + scope.getValue() + " token";
        logRequest(requestName, httpPost);
        Response response = sendRequest(httpClient, httpPost, requestName, HttpStatus.SC_OK);

        try {
            return new ObjectMapper().readValue(response.getBody(), AccessTokenDTO.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getBearerToken(AccessTokenDTO accessToken) {
        String token = null;
        if (Objects.equals(accessToken.getTokenType(), "bearer") && accessToken.getAccessToken() != null) {
            token = accessToken.getTokenType() + " " + accessToken.getAccessToken();
        } else {
            log.error("Bearer token is not valid.");
        }
        return token;
    }
}