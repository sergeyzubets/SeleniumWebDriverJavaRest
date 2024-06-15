package com.coherentsolutions.utils;

import com.coherentsolutions.data.Gender;
import com.coherentsolutions.data.dto.AccessTokenDTO;
import com.coherentsolutions.data.dto.UserDTO;
import com.coherentsolutions.data.models.Request;
import com.coherentsolutions.data.models.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.RequestLine;

import java.io.IOException;
import java.util.Random;

@Slf4j
public class GeneralUtil {
    private static final Faker FAKER = new Faker();
    private static final long WHILE_LIFETIME_SEC = 20;
    private static final int MAX_USER_AGE = 120;

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

    public static String getRandomZipCode() {
        return FAKER.address().zipCode();
    }

    public static String convertEntityToBody(HttpEntity stringEntity) {
        String requestBody = null;
        try {
            requestBody = EntityUtils.toString(stringEntity);
        } catch (IOException | ParseException e) {
            log.error(e.getMessage());
        }
        return requestBody;
    }

    public static String getRandomUserName() {
        return FAKER.name().fullName();
    }

    public static Gender getRandomGender() {
        int i = new Random().nextInt(2);
        return i == 1 ? Gender.MALE : Gender.FEMALE;
    }

    public static Integer getRandomAge() {
        return new Random().nextInt(MAX_USER_AGE);
    }

    public static String convertUserToJsonBody(UserDTO user) {
        String requestBody = null;
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            requestBody = objectWriter.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return requestBody;
    }

    public static boolean keepWhile(long whileStartTime) {
        boolean result = true;
        if (System.currentTimeMillis() >= whileStartTime + WHILE_LIFETIME_SEC * 1000) {
            log.error("While was interrupted by timeout.");
            result = false;
        }
        return result;
    }
}