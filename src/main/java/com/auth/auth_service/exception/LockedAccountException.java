package com.auth.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class LockedAccountException extends RuntimeException {
    public LockedAccountException(String message) {
        super(message);
    }
}
