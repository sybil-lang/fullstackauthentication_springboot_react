package com.authify.authenticationsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex
    ) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", ex.getStatusCode().value());
        error.put("error", ex.getStatusCode());
        error.put("message", ex.getReason());

        return new ResponseEntity<>(
                error,
                HttpStatus.valueOf(ex.getStatusCode().value())
        );
    }
}