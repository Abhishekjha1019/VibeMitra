package com.vibemitra.common;

import com.vibemitra.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps exceptions to a consistent JSON error body. Internal details (stack traces, SQL)
 * are never returned to the client.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return body(HttpStatus.BAD_REQUEST, "Validation failed", fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
        return body(HttpStatus.BAD_REQUEST, "Malformed request body", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return body(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<Map<String, Object>> handleForbidden(AccessDeniedException ex) {
        return body(HttpStatus.FORBIDDEN, "Access denied", null);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    private static ResponseEntity<Map<String, Object>> body(HttpStatus status, String message,
                                                            Map<String, String> fields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("message", message);
        if (fields != null && !fields.isEmpty()) {
            body.put("fields", fields);
        }
        return ResponseEntity.status(status).body(body);
    }
}
