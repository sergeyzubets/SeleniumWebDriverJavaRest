package com.coherentsolutions.data.businessObjects;

import com.coherentsolutions.data.dto.ZipCodeDTO;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.util.List;
import java.util.Random;

import static com.coherentsolutions.utils.GeneralUtil.getRandomZipCode;


@Slf4j
public class ZipCodeClientBO extends BaseBO {

    @Step("Adding unique zip code to the application.")
    public ZipCodeDTO addUniqueZipCode(CloseableHttpClient httpClient) {
        ZipCodeDTO newZipCode = getNonexistentZipCode(httpClient);
        List<ZipCodeDTO> zipCodesToAdd = List.of(newZipCode);
        zipCodeClient.createZipCodes(httpClient, zipCodesToAdd);
        return newZipCode;
    }

    @Step("Getting non-existent in the application zip code.")
    public ZipCodeDTO getNonexistentZipCode(CloseableHttpClient httpClient) {
        List<ZipCodeDTO> availableZipCodes = getAvailableZipCodes(httpClient);

        long whileStartTime = System.currentTimeMillis();
        ZipCodeDTO nonexistentZipCode;

        while (true) {
            nonexistentZipCode = new ZipCodeDTO(getRandomZipCode());
            if (!availableZipCodes.contains(nonexistentZipCode)) {
                break;
            }

            if (System.currentTimeMillis() >= whileStartTime + WHILE_LIFETIME_SEC * 1000) {
                log.error(WHILE_INTERRUPTION_MESSAGE);
                break;
            }
        }
        return nonexistentZipCode;
    }

    @Step("Getting all available in the application zip codes.")
    public List<ZipCodeDTO> getAvailableZipCodes(CloseableHttpClient httpClient) {
        return (List<ZipCodeDTO>) zipCodeClient.getZipCodes(httpClient).getParsedBody();
    }

    @Step("Getting duplicate for available in the application zip codes.")
    public ZipCodeDTO getDuplicate(CloseableHttpClient httpClient) {
        List<ZipCodeDTO> availableZipCodes = getAvailableZipCodes(httpClient);
        return availableZipCodes.get(new Random().nextInt(availableZipCodes.size() - 1));
    }
}