package com.coherentsolutions.utils;

import com.coherentsolutions.dto.AccessTokenDTO;
import com.coherentsolutions.models.Request;
import com.coherentsolutions.models.Response;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.message.RequestLine;

@Slf4j
public class GeneralUtil {
    protected static final long WHILE_LIFETIME_SEC = 20;
    protected static final String WHILE_INTERRUPTION_MESSAGE = "While was interrupted by timeout.";
    protected static final Faker FAKER = new Faker();

    public static void logTokenDetails(AccessTokenDTO token, String scope) {
        log.warn("{} access token expires in {} hours.", scope.toUpperCase(), String.format("%.3f", (double) token.getExpiresIn() / 3600));
    }

    @Step("Sending request.")
    public static void logRequest(String name, HttpUriRequestBase request) {
        log.info(new Request("'" + name + "' request", new RequestLine(request), request.getHeaders(), request.getEntity()).toString());
    }

    @Step("Sending request.")
    public static void logRequest(String name, HttpUriRequestBase request, String requestBody) {
        log.info(new Request("'" + name + "' request", new RequestLine(request), request.getHeaders(), request.getEntity(), requestBody).toString());
    }

    @Step("Getting response.")
    public static void logResponse(String name, ClassicHttpResponse response, String responseBody) {
        log.info(new Response("'" + name + "' response", response.getVersion().format(), response.getHeaders(), response.getEntity(), responseBody).toString());
    }
}