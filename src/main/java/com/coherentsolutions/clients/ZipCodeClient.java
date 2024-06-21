package com.coherentsolutions.clients;

import com.coherentsolutions.data.dto.ZipCodeDTO;
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

import static com.coherentsolutions.utils.GeneralUtil.convertEntityToBody;
import static com.coherentsolutions.utils.GeneralUtil.logRequest;

@Slf4j
public class ZipCodeClient extends BaseClient {

    public Response getZipCodes(CloseableHttpClient httpClient, int code) {
        String path = "/zip-codes";
        URI uri = getUri(path);

        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Authorization", HttpClient.getReadToken());
        httpGet.addHeader("accept", "*/*");
        String requestName = "Get available zip codes";
        logRequest(requestName, httpGet);
        Response response = sendRequest(httpClient, httpGet, requestName, code);

        try {
            List<ZipCodeDTO> zipCodes = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {
            });
            return new Response(response.getCode(), zipCodes);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Response createZipCodes(CloseableHttpClient httpClient, List<ZipCodeDTO> zipCodesToAdd, int code) {
        String path = "/zip-codes/expand";
        URI uri = getUri(path);

        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Authorization", HttpClient.getWriteToken());
        httpPost.addHeader("accept", "*/*");
        httpPost.addHeader("Content-Type", "application/json");

        HttpEntity stringEntity = new StringEntity(zipCodesToAdd.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        String requestBody = convertEntityToBody(stringEntity);
        String requestName = "Expand available zip codes";
        logRequest(requestName, httpPost, requestBody);
        Response response = sendRequest(httpClient, httpPost, requestName, code);

        try {
            List<ZipCodeDTO> zipCodes = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {
            });
            return new Response(response.getCode(), zipCodes);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}