package com.coherentsolutions.clients;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

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
}