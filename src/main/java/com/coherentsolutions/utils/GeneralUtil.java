package com.coherentsolutions.utils;

import com.coherentsolutions.dto.AccessTokenDTO;
import com.coherentsolutions.models.Request;
import com.coherentsolutions.models.Response;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.message.RequestLine;

@Slf4j
public class GeneralUtil {

    public static void logTokenDetails(AccessTokenDTO token, String scope) {
        log.warn("{} access token expires in {} hours.", scope.toUpperCase(), String.format("%.3f", (double) token.getExpiresIn() / 3600));
    }

    @Step("Sending request.")
    public static void logRequest(String requestName, HttpUriRequestBase request) {
        log.info(new Request("'" + requestName + "' request", new RequestLine(request), request.getHeaders(), request.getEntity()).toString());
    }

    @Step("Getting response.")
    public static void logResponse(String responseName, ClassicHttpResponse response, String responseBody) {
        log.info(new Response("'" + responseName + "' response", response.getVersion().format(), response.getHeaders(), response.getEntity(), responseBody).toString());
    }
}