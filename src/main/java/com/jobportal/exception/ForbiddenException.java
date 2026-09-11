package com.jobportal.exception;

public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(message);
    }
}