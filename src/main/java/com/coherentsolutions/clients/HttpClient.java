package com.coherentsolutions.clients;

import com.coherentsolutions.models.User;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.util.List;

import static com.coherentsolutions.utils.JsonUtil.readUserCredentialsFile;

@Slf4j
@Getter
public class HttpClient {
    private static final String HOST = System.getProperty("host");
    private static final int PORT = Integer.parseInt(System.getProperty("port"));
    private static HttpClient instance = null;
    private final CloseableHttpClient httpClient;

    private HttpClient() {
        httpClient = configureHttpClient();
    }

    public static HttpClient getHttpClientInstance() {
        if (instance == null) {
            synchronized (HttpClient.class) {
                if (instance == null) {
                    instance = new HttpClient();
                }
            }
        }
        return instance;
    }

    @Step("Http client instance has been closed.")
    public void closeHttpClient() {
        try {
            getHttpClientInstance().getHttpClient().close();
            log.info("Http client instance has been closed.");
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Step("Http client instance has been created.")
    private CloseableHttpClient configureHttpClient() {
        return HttpClients.custom()
                .setDefaultCredentialsProvider(configureCredentialsProvider())
                .build();
    }

    private BasicCredentialsProvider configureCredentialsProvider() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        List<User> listOfUsers = readUserCredentialsFile();

        listOfUsers.forEach(user -> credentialsProvider.setCredentials(
                new AuthScope(HOST, PORT),
                new UsernamePasswordCredentials(user.getUsername(), user.getPassword().toCharArray())));
        return credentialsProvider;
    }
}