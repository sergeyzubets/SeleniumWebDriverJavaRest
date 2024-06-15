package com.coherentsolutions.data.models;

import lombok.Getter;

@Getter
public class Parameter {
    private final String key;
    private final String value;

    public Parameter(String key, String value) {
        this.key = key;
        this.value = value;
    }
}