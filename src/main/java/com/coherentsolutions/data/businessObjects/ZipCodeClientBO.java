package com.coherentsolutions.data.businessObjects;

import com.coherentsolutions.data.dto.ZipCodeDTO;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.util.List;
import java.util.Random;

import static com.coherentsolutions.utils.GeneralUtil.getRandomZipCode;
import static com.coherentsolutions.utils.GeneralUtil.keepWhile;


@Slf4j
public class ZipCodeClientBO extends BaseBO {

    @Step("Getting non-existent in the application zip code.")
    public ZipCodeDTO getNonexistentZipCode(CloseableHttpClient httpClient, int code) {
        List<ZipCodeDTO> availableZipCodes = getAvailableZipCodes(httpClient, code);
        ZipCodeDTO nonexistentZipCode = null;
        boolean running = true;
        long whileStartTime = System.currentTimeMillis();

        while (running) {
            nonexistentZipCode = new ZipCodeDTO(getRandomZipCode());
            if (!availableZipCodes.contains(nonexistentZipCode)) {
                break;
            }
            running = keepWhile(whileStartTime);
        }
        return nonexistentZipCode;
    }

    @Step("Getting all available in the application zip codes.")
    public List<ZipCodeDTO> getAvailableZipCodes(CloseableHttpClient httpClient, int code) {
        return (List<ZipCodeDTO>) zipCodeClient.getZipCodes(httpClient, code).getParsedBody();
    }

    @Step("Getting duplicate for available in the application zip codes.")
    public ZipCodeDTO getDuplicate(CloseableHttpClient httpClient, int code) {
        List<ZipCodeDTO> availableZipCodes = getAvailableZipCodes(httpClient, code);
        return availableZipCodes.get(new Random().nextInt(availableZipCodes.size() - 1));
    }
}