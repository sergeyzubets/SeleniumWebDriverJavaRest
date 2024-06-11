package com.coherentsolutions.clients;

import com.coherentsolutions.dto.AccessTokenDTO;
import com.coherentsolutions.utils.Scope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static com.coherentsolutions.utils.GeneralUtil.*;

@Slf4j
@Getter
public class AuthorizationClient extends BaseClient{
    private CloseableHttpResponse response;
    private AccessTokenDTO accessTokenDTO;

    public String getAccessToken(CloseableHttpClient httpClient, Scope scope) {
        sendAccessTokenRequest(httpClient, scope);
        logTokenDetails(accessTokenDTO, scope.getValue());
        return getBearerToken(accessTokenDTO);
    }

    public CloseableHttpResponse sendAccessTokenRequest(CloseableHttpClient httpClient, Scope scope) {
        String path = "/oauth/token";
        Map<String, String> paramMap = Map.of("grant_type", "client_credentials", "scope", scope.getValue());
        URI uri = getUri(path, paramMap);

        HttpPost httpPost = new HttpPost(uri);
        String requestName = "Get " + scope.getValue() + " token";
        logRequest(requestName, httpPost);

        try {
            response = (CloseableHttpResponse) httpClient.execute(httpPost, response1 -> {
                int statusCode = response1.getCode();
                if (statusCode != HttpStatus.SC_OK) {
                    logIncorrectResponseCode(HttpStatus.SC_OK, statusCode);
                }

                HttpEntity entity = response1.getEntity();
                if (entity == null) {
                    logNullEntity();
                } else {
                    String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    logResponse(requestName, response1, responseBody);
                    accessTokenDTO = new ObjectMapper().readValue(responseBody, AccessTokenDTO.class);
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