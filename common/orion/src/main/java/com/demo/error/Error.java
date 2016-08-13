package com.demo.error;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {

    private String key;
    private String message;

    public Error(@JsonProperty("key") String key, @JsonProperty("message") String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return key + " : " + message;
    }
}

