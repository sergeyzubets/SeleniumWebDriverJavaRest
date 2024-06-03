package com.coherentsolutions.utils;

import com.coherentsolutions.clients.ZipCodeClient;
import com.coherentsolutions.dto.ZipCodeDTO;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ZipCodeClientUtil extends GeneralUtil {

    @Step("Adding unique Zip Code to the application.")
    public static ZipCodeDTO addUniqueZipCodeToApp(CloseableHttpClient httpClient, String readToken, String writeToken) {
        ZipCodeDTO newZipCode = getNonexistentZipCode(httpClient, readToken);
        List<ZipCodeDTO> zipCodesToAdd = List.of(newZipCode);
        new ZipCodeClient().sendPostExpandZipCodesRequest(httpClient, writeToken, zipCodesToAdd);
        return newZipCode;
    }

    public static ZipCodeDTO getNonexistentZipCode(CloseableHttpClient httpClient, String readToken) {
        ZipCodeClient zipCodeClient = new ZipCodeClient();
        zipCodeClient.sendGetAvailableZipCodesRequest(httpClient, readToken);
        List<ZipCodeDTO> availableZipCodes = zipCodeClient.getZipCodes();

        long whileStartTime = System.currentTimeMillis();
        ZipCodeDTO nonexistentZipCode;

        while (true) {
            nonexistentZipCode = new ZipCodeDTO(getRandomZipCode());
            if (!availableZipCodes.contains(nonexistentZipCode)) {
                break;
            }

            if (System.currentTimeMillis() >= whileStartTime + WHILE_LIFETIME_SEC * 1000) {
                log.warn(WHILE_INTERRUPTION_MESSAGE);
                break;
            }
        }
        return nonexistentZipCode;
    }

    public static String getRandomZipCode() {
        return FAKER.address().zipCode();
    }

    public static ZipCodeDTO getDuplicatedAvailableZipCode(CloseableHttpClient httpClient, String readToken) {
        List<ZipCodeDTO> availableZipCodes = getAvailableZipCodes(httpClient, readToken);
        return availableZipCodes.get(new Random().nextInt(availableZipCodes.size() - 1));
    }

    public static List<ZipCodeDTO> getAvailableZipCodes(CloseableHttpClient httpClient, String readToken) {
        ZipCodeClient zipCodeClient = new ZipCodeClient();
        zipCodeClient.sendGetAvailableZipCodesRequest(httpClient, readToken);
        return zipCodeClient.getZipCodes();
    }

    public static int getZipCodeRepetitionCount(List<ZipCodeDTO> zipCodes, ZipCodeDTO zipCodeToFind) {
        AtomicInteger repetitionCount = new AtomicInteger();
        zipCodes.forEach(zipCode -> {
            if (zipCode.equals(zipCodeToFind)) {
                repetitionCount.getAndIncrement();
            }
        });
        return repetitionCount.get();
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
}