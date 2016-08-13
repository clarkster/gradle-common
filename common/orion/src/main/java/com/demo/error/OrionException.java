package com.demo.error;

import org.springframework.http.HttpStatus;

/**
 * Base class for Exceptions to throw, translated into a response matching the Orion definition.
 * Exceptions contain the HttpStatus code to return, plus an {@link ErrorMessage} with the details.
 */
public class OrionException extends RuntimeException {

    private final ErrorMessage errors;
    private final HttpStatus httpStatus;

    public OrionException(HttpStatus httpStatus, ErrorMessage errors) {
        this.httpStatus = httpStatus;
        this.errors = errors;
    }

    public OrionException(HttpStatus httpStatus, String errorMessage) {
        this(httpStatus, new ErrorMessage(httpStatus.getReasonPhrase(), errorMessage));
    }

    @Override
    public String getMessage() {
        return httpStatus.toString() + " " + errors.toString();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ErrorMessage getErrors() {
        return errors;
    }
}