package com.coherentsolutions.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

    FEMALE("FEMALE"),
    MALE("MALE");

    private final String gender;

}