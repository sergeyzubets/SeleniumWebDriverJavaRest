package com.coherentsolutions.models;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;

import java.util.Arrays;

public class Response {
    private final String response;
    private final String statusLine;
    private final Header[] headers;
    private final HttpEntity httpEntity;
    private final String responseBody;

    public Response(String response, String statusLine, Header[] headers, HttpEntity httpEntity, String responseBody) {
        this.response = response;
        this.statusLine = statusLine;
        this.headers = headers;
        this.httpEntity = httpEntity;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return "response = " + response +
                "\nstatusLine = " + statusLine +
                "\nheaders = " + Arrays.toString(headers) +
                "\nhttpEntity = " + httpEntity +
                "\nresponseBody = " + responseBody;
    }
}