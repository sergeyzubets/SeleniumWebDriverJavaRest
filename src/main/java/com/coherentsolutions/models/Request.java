package com.coherentsolutions.models;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.RequestLine;

import java.util.Arrays;

@Slf4j
public class Request {
    private final String requestName;
    private final RequestLine requestLine;
    private final Header[] headers;
    private final HttpEntity httpEntity;
    private String requestBody = "empty";

    public Request(String requestName, RequestLine requestLine, Header[] headers, HttpEntity httpEntity) {
        this.requestName = requestName;
        this.requestLine = requestLine;
        this.headers = headers;
        this.httpEntity = httpEntity;
    }

    public Request(String requestName, RequestLine requestLine, Header[] headers, HttpEntity httpEntity, String requestBody) {
        this.requestName = requestName;
        this.requestLine = requestLine;
        this.headers = headers;
        this.httpEntity = httpEntity;
        this.requestBody = requestBody;
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