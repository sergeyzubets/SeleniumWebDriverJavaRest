package com.coherentsolutions.clients;

import com.coherentsolutions.dto.ZipCodeDTO;
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
import static com.coherentsolutions.utils.ZipCodeClientUtil.convertEntityToBody;

@Slf4j
@Getter
public class ZipCodeClient extends BaseClient {
    private CloseableHttpResponse response;
    private List<ZipCodeDTO> zipCodes;

    public CloseableHttpResponse sendGetAvailableZipCodesRequest(CloseableHttpClient httpClient, String readToken) {
        String path = "/zip-codes";
        URI uri = getUri(path);

        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Authorization", readToken);
        httpGet.addHeader("accept", "*/*");
        String requestName = "Get available zip codes";
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
                    zipCodes = new ObjectMapper().readValue(responseBody, new TypeReference<>() {
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

    public CloseableHttpResponse sendPostExpandZipCodesRequest(CloseableHttpClient httpClient, String writeToken, List<ZipCodeDTO> zipCodesToAdd) {
        String path = "/zip-codes/expand";
        URI uri = getUri(path);

        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Authorization", writeToken);
        httpPost.addHeader("accept", "*/*");
        httpPost.addHeader("Content-Type", "application/json");

        HttpEntity stringEntity = new StringEntity(zipCodesToAdd.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        String requestBody = convertEntityToBody(stringEntity);
        String requestName = "Expand available zip codes";
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
                    zipCodes = new ObjectMapper().readValue(responseBody, new TypeReference<>() {
                    });
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
}