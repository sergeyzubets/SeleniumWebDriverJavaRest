package com.coherentsolutions.data.businessObjects;

import com.coherentsolutions.clients.UserClient;
import com.coherentsolutions.clients.ZipCodeClient;

public class BaseBO {
    protected static final long WHILE_LIFETIME_SEC = 20;
    protected static final String WHILE_INTERRUPTION_MESSAGE = "While was interrupted by timeout.";
    protected final ZipCodeClient zipCodeClient;
    protected final UserClient userClient;

    public BaseBO() {
        zipCodeClient = new ZipCodeClient();
        userClient = new UserClient();
    }
}