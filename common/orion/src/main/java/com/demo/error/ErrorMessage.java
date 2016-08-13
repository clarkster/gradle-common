package com.demo.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ErrorMessage {

    private List<Error> errors;

    public ErrorMessage(@JsonProperty("errors") List<Error> errors) {
        this.errors = errors;
    }

    public ErrorMessage(String code, String message) {
        this.errors = Collections.singletonList(new Error(code, message));
    }

    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return errors.toString();
    }
}
