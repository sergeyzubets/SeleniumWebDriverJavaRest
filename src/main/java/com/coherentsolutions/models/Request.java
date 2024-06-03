package com.coherentsolutions.models;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.RequestLine;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class Request {
    private final String requestName;
    private final RequestLine requestLine;
    private final Header[] headers;
    private final HttpEntity httpEntity;
    private String requestBody;

    public Request(String requestName, RequestLine requestLine, Header[] headers, HttpEntity httpEntity) {
        this.requestName = requestName;
        this.requestLine = requestLine;
        this.headers = headers;
        this.httpEntity = httpEntity;
        this.requestBody = getRequestBody(httpEntity);
    }

    private String getRequestBody(HttpEntity httpEntity) {
        String result = null;

        try {
            result = httpEntity != null ? requestBody = EntityUtils.toString(httpEntity) : "empty";
        } catch (IOException | ParseException e) {
            log.error(e.getMessage());
        }
        return result;
    }

    @Override
    public String toString() {
        return "requestName = " + requestName +
                "\nrequestLine = " + requestLine +
                "\nheaders = " + Arrays.toString(headers) +
                "\nhttpEntity = " + httpEntity +
                "\nrequestBody = " + requestBody;
    }
}