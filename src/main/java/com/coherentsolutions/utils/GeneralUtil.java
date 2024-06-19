package com.coherentsolutions.utils;

import com.coherentsolutions.data.Gender;
import com.coherentsolutions.data.dto.AccessTokenDTO;
import com.coherentsolutions.data.dto.UpdateUserDTO;
import com.coherentsolutions.data.models.Environment;
import com.coherentsolutions.data.models.Parameter;
import com.coherentsolutions.data.models.Request;
import com.coherentsolutions.data.models.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.RequestLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Slf4j
public class GeneralUtil {
    private static final Faker FAKER = new Faker();
    private static final long WHILE_LIFETIME_SEC = 20;
    private static final int MAX_USER_AGE = 120;
    private static final Path USER_CREDENTIALS_FILE = Paths.get(System.getProperty("allure.results.directory"), "environment.xml");

    public static void logTokenDetails(AccessTokenDTO token, String scope) {
        log.warn("{} access token expires in {} hours.", scope.toUpperCase(), String.format("%.3f", (double) token.getExpiresIn() / 3600));
    }

    @Step("Sending request.")
    public static void logRequest(String name, HttpUriRequestBase request) {
        String requestName = String.format("'%s' request", name);
        log.info(new Request(requestName, new RequestLine(request), request.getHeaders(), request.getEntity()).toString());
    }

    @Step("Sending request.")
    public static void logRequest(String name, HttpUriRequestBase request, String requestBody) {
        String requestName = String.format("'%s' request", name);
        log.info(new Request(requestName, new RequestLine(request), request.getHeaders(), request.getEntity(), requestBody).toString());
    }

    @Step("Getting response.")
    public static void logResponse(int code, String name, ClassicHttpResponse response, String responseBody) {
        String responseName = String.format("'%s' response", name);
        String statusLine = response.getVersion().format();
        log.info(new Response(responseName, code, statusLine, response.getHeaders(), response.getEntity(), responseBody).toString());
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

    public static <T> String convertObjectToJson(T user) {
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

    public static void writeAllureEnvironmentFile(List<Parameter> listOfParameters) {
        try {
            new XmlMapper().writeValue(new File(USER_CREDENTIALS_FILE.toString()), new Environment(listOfParameters));
            log.info("Allure environment data saved.");
        } catch (IOException e) {
            log.error("Allure environment data save failed: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String removeLineFromBody(UpdateUserDTO updateUser) {
        return convertObjectToJson(updateUser).replace(" \"zipCode\" : null,", "");
    }
}