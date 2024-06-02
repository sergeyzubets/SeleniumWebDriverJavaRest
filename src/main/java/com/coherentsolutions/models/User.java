package com.coherentsolutions.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class User {

    @JsonProperty
    private String username;
    @JsonProperty
    private String password;

    @Override
    public String toString() {
        return "username=" + username + ", password=" + password;
    }
}