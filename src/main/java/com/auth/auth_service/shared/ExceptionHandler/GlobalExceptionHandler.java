package com.auth.auth_service.shared.ExceptionHandler;

import com.auth.auth_service.dto.GenericErrorResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericErrorResponse> handleGlobalException(Exception ex, WebRequest request) {

        if (AnnotationUtils.findAnnotation
                (ex.getClass(), ResponseStatus.class) != null) {

            HttpStatus status = Objects.requireNonNull(AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class)).value();

            return ResponseEntity
                    .status(status)
                    .body(
                            new GenericErrorResponse(
                                LocalDateTime.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getDescription(false)
                            )
                    );
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(
                            new GenericErrorResponse(
                                    LocalDateTime.now(),
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                    ex.getMessage(),
                                    request.getDescription(false)
                            )
                    );
        }
    }

}
