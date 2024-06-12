package com.coherentsolutions.clients;

import com.coherentsolutions.models.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.coherentsolutions.utils.GeneralUtil.logResponse;
import static java.util.stream.Collectors.toList;

@Slf4j
public abstract class BaseClient {
    protected static final String SCHEME = System.getProperty("scheme");
    protected static final String HOST = System.getProperty("host");
    protected static final int PORT = Integer.parseInt(System.getProperty("port"));

    protected void codeValidation(int expected, int actual) {
        try {
            if (expected != actual) {
                throw new AssertionError();
            }
        } catch (AssertionError e) {
            log.error("Response code is not valid. Expected {}, actual {}", expected, actual);
        }
    }

    protected void logNullEntity() {
        log.warn("Response contains no content.");
    }

    private void logNullURI() {
        log.error("Uri is null.");
    }

    private void logNullResponse() {
        log.error("Response is null.");
    }

    protected void releaseRequestResources(String requestName, CloseableHttpResponse response, HttpUriRequestBase request) {
        try {
            log.info("Releasing any system resources associated with '{}' request.", requestName);
            EntityUtils.consume(response.getEntity());
            response.close();
            request.reset();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    protected URI getUri(String path) {
        URI uri;
        try {
            uri = new URIBuilder().setScheme(SCHEME).setHost(HOST).setPort(PORT).setPath(path).build();

            if(uri == null) {
                logNullURI();
                throw new NullPointerException();
            }
        } catch (URISyntaxException | NullPointerException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return uri;
    }

    protected URI getUri(String path, Map<String, String> paramMap) {
        URI uri;
        try {
            uri = new URIBuilder()
                    .setScheme(SCHEME)
                    .setHost(HOST)
                    .setPort(PORT)
                    .addParameters(paramMap.entrySet()
                                    .stream()
                                    .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                                    .collect(toList()))
                    .setPath(path)
                    .build();

            if(uri == null) {
                logNullURI();
            }
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return uri;
    }

    protected Response sendRequest(CloseableHttpClient httpClient, HttpUriRequestBase request, String requestName, int expectedCode) {
        CloseableHttpResponse closeableHttpResponse = null;
        AtomicReference<String> body = new AtomicReference<>();
        Response response = new Response();

        try {
            closeableHttpResponse = (CloseableHttpResponse) httpClient.execute(request, response1 -> {
                int statusCode = response1.getCode();
                response.setCode(statusCode);
                codeValidation(expectedCode, statusCode);

                HttpEntity entity = response1.getEntity();
                if (entity == null) {
                    logNullEntity();
                } else {
                    body.set(EntityUtils.toString(entity, StandardCharsets.UTF_8));
                    logResponse(requestName, response1, body.get());
                    response.setBody(body.get());
                }
                return response1;
            });

            if(closeableHttpResponse == null) {
                logNullResponse();
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } finally {
            releaseRequestResources(requestName, closeableHttpResponse, request);
        }
        return response;
    }
}