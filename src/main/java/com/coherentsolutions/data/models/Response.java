package com.coherentsolutions.data.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Response {
    private int code;
    private String name;
    private String statusLine;
    private Header[] headers;
    private HttpEntity httpEntity;
    private String body;
    private FailedResponseBody failedBody;
    private List<?> parsedBody;

    public Response(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public Response(int code, FailedResponseBody failedBody) {
        this.code = code;
        this.failedBody = failedBody;
    }

    public Response(int code, List<?> parsedBody) {
        this.code = code;
        this.parsedBody = parsedBody;
    }

    public Response(String name, int code, String statusLine, Header[] headers, HttpEntity httpEntity, String body) {
        this.name = name;
        this.code = code;
        this.statusLine = statusLine;
        this.headers = headers;
        this.httpEntity = httpEntity;
        this.body = body.isEmpty() ? "empty" : body;
    }

    @Override
    public String toString() {
        return String.format("name = %s\nstatusLine = %s\nresponseCode = %s\nheaders = %s\nhttpEntity = %s\nresponseBody = %s",
                name, statusLine, code, Arrays.toString(headers), httpEntity, body);
    }
}