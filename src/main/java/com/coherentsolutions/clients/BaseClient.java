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

    protected static final String RESPONSE_CODE_FAILURE = "Response code is not valid. Expected {}, received {}";
    protected static final String NULLABLE_ENTITY = "Response contains no content.";
    protected static final String RELEASE_RESOURCES = "Releasing any system resources associated with '{}' request.";

    protected void logIncorrectResponseCode(int expected, int actual) {
        log.error(RESPONSE_CODE_FAILURE, expected, actual);
    }

    protected void logNullEntity() {
        log.warn(NULLABLE_ENTITY);
    }

    protected void releaseRequestResources(String requestName, CloseableHttpResponse response, HttpUriRequestBase request) throws IOException {
        log.info(RELEASE_RESOURCES, requestName);
        EntityUtils.consume(response.getEntity());
        response.close();
        request.reset();
    }
}