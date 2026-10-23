package com.example.ratelimiter.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates application exceptions into proper HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> handleRateLimitExceeded(RateLimitExceededException ex) {
        logger.warn("Rate limit exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        logger.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred.");
    }
}