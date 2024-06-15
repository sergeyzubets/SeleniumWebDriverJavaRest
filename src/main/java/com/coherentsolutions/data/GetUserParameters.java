package com.coherentsolutions.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GetUserParameters {

    OLDER_THAN("olderThan"),
    YOUNGER_THAN("youngerThan"),
    SEX("sex");

    private final String value;
}