package com.coherentsolutions.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ZipCodeDTO {
    private String code;

    @Override
    public String toString() {
        return "\"" + code + "\"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ZipCodeDTO)) {
            return false;
        }

        if (o == null) {
            return false;
        }

        ZipCodeDTO that = (ZipCodeDTO) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}