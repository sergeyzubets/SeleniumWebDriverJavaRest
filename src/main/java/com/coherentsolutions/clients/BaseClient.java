package com.coherentsolutions.clients;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
public abstract class BaseClient {
    protected static final String SCHEME = System.getProperty("scheme");
    protected static final String HOST = System.getProperty("host");
    protected static final int PORT = Integer.parseInt(System.getProperty("port"));

    protected void logIncorrectResponseCode(int expected, int actual) {
        log.error("Response code is not valid. Expected {}, received {}", expected, actual);
    }

    protected void logNullEntity() {
        log.warn("Response contains no content.");
    }

    private void logNullURI() {
        log.error("Uri is null.");
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
            uri = new URIBuilder()
                    .setScheme(System.getProperty("scheme"))
                    .setHost(System.getProperty("host"))
                    .setPort(Integer.parseInt(System.getProperty("port")))
                    .setPath(path)
                    .build();

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
                throw new NullPointerException();
            }
        } catch (URISyntaxException | NullPointerException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return uri;
    }
}