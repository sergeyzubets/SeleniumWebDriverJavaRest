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

import java.util.List;

import static com.coherentsolutions.utils.JsonUtil.readUserCredentialsFile;

@Slf4j
@Getter
public class HttpClient {
    private static ThreadLocal<CloseableHttpClient> threadLocal = new ThreadLocal<>();

    private HttpClient() {
    }

    public static CloseableHttpClient getHttpClientInstance() {
        if (threadLocal.get() != null) {
            return threadLocal.get();
        }

        CloseableHttpClient httpClient = configureHttpClient();
        threadLocal.set(httpClient);
        return threadLocal.get();
    }

    @Step("Http client instance has been closed.")
    public static void closeHttpClient() {
        try {
            if (threadLocal.get() != null) {
                threadLocal.get().close();
                log.info("Http client instance has been closed.");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            threadLocal.remove();
        }
    }

    @Step("Http client instance has been created.")
    private static CloseableHttpClient configureHttpClient() {
        return HttpClients.custom()
                .setDefaultCredentialsProvider(configureCredentialsProvider())
                .build();
    }

    private static BasicCredentialsProvider configureCredentialsProvider() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        List<User> listOfUsers = readUserCredentialsFile();

        listOfUsers.forEach(user -> credentialsProvider.setCredentials(
                new AuthScope(System.getProperty("host"), Integer.parseInt(System.getProperty("port"))),
                new UsernamePasswordCredentials(user.getUsername(), user.getPassword().toCharArray())));
        return credentialsProvider;
    }
}