package com.coherentsolutions.clients;

import com.coherentsolutions.dto.AccessTokenDTO;
import com.coherentsolutions.utils.Scope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.coherentsolutions.utils.GeneralUtil.*;

@Slf4j
public class AuthorizationClient {
    private static final String NULLABLE_ENTITY = "Response contains no content.";
    private static final String RELEASE_RESOURCES = "Releasing any system resources associated with ";
    private static final String REQUEST_NAME = "Get bearer token";
    private static final String INVALID_TOKEN = "Bearer token is not valid.";
    private static final String RESPONSE_CODE_FAILURE = "Response code is not valid.";

    private HttpEntity entity;
    private AccessTokenDTO accessToken;

    public String getAccessToken(CloseableHttpClient httpClient, Scope scope) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme(System.getProperty("scheme"))
                .setHost(System.getProperty("host"))
                .setPort(Integer.parseInt(System.getProperty("port")))
                .setPath("/oauth/token")
                .setParameter("grant_type", "client_credentials")
                .setParameter("scope", scope.getValue())
                .build();

        HttpPost httpPost = new HttpPost(uri);
        logRequest(REQUEST_NAME, httpPost);

        try {
            httpClient.execute(httpPost, response -> {
                int statusCode = response.getCode();

                if (statusCode != HttpStatus.SC_OK) {
                    log.error(RESPONSE_CODE_FAILURE);
                }

                entity = response.getEntity();

                if (entity == null) {
                    log.warn(NULLABLE_ENTITY);
                } else {
                    String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    accessToken = new ObjectMapper().readValue(responseBody, AccessTokenDTO.class);
                    logResponse(REQUEST_NAME, response, responseBody);
                    logTokenDetails(accessToken, scope.getValue());
                }
                return response;
            });

        } catch (IOException ex) {
            log.error(ex.getMessage());
        } finally {
            log.info(RELEASE_RESOURCES + "'" + REQUEST_NAME + "' request");
            EntityUtils.consume(entity);
            httpPost.reset();
        }
        return getBearerToken(accessToken);
    }

    private String getBearerToken(AccessTokenDTO accessToken) {
        String token = null;
        if (Objects.equals(accessToken.getTokenType(), "bearer") && accessToken.getAccessToken() != null) {
            token = accessToken.getTokenType() + " " + accessToken.getAccessToken();
        } else {
            log.error(INVALID_TOKEN);
        }
        return token;
    }
}