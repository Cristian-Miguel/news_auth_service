package com.auth.auth_service.auth.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailWasAlreadyValidatedException extends RuntimeException {
    public EmailWasAlreadyValidatedException(String message) {
        super(message);
    }
}
