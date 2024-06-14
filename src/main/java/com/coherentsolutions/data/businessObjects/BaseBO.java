package com.coherentsolutions.data.businessObjects;

import com.coherentsolutions.clients.UserClient;
import com.coherentsolutions.clients.ZipCodeClient;

public class BaseBO {
    protected final ZipCodeClient zipCodeClient;
    protected final UserClient userClient;

    public BaseBO() {
        zipCodeClient = new ZipCodeClient();
        userClient = new UserClient();
    }
}