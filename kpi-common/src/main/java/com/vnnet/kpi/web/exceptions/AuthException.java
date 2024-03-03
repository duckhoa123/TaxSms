package com.vnnet.kpi.web.exceptions;

public class AuthException extends RuntimeException {
    public AuthException(String errorMessage) {
        super(errorMessage);
    }

    public AuthException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
