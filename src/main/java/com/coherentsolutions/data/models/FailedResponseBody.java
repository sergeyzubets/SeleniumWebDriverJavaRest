package com.coherentsolutions.data.models;

import lombok.Getter;

import java.util.Objects;

@Getter
public class FailedResponseBody {
    private String ts;
    private String message;

    @Override
    public String toString() {
        return "Failed Response Body message: " + message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FailedResponseBody)) {
            return false;
        }

        if (o == null) {
            return false;
        }

        FailedResponseBody that = (FailedResponseBody) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}