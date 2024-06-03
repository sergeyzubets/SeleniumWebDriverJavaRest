package com.coherentsolutions.utils;

import com.coherentsolutions.models.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class JsonUtil {
    private static final String PARSED_DATA_EQUALS_NULL = "Parsed data equals null.";
    private static final Path USER_CREDENTIALS_FILE = Paths.get("src", "main", "resources", "json", "users.json");

    public static List<User> readUserCredentialsFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> result = null;

        try {
            File file = new File(USER_CREDENTIALS_FILE.toString());
            result = objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        if (result == null) {
            log.error(PARSED_DATA_EQUALS_NULL);
        }
        return result;
    }
}