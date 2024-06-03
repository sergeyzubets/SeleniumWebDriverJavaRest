package com.coherentsolutions.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Scope {
    READ("read"),
    WRITE("write");

    private final String value;
}