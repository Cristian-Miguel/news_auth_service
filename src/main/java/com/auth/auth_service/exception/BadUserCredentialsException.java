package com.auth.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BadUserCredentialsException extends RuntimeException {
    public BadUserCredentialsException(String message) {
        super(message);
    }
}
