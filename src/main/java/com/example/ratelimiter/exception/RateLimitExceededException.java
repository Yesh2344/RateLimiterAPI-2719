package com.example.ratelimiter.exception;

/**
 * Thrown when a client exceeds the configured request rate.
 */
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
// tiny readability tweak
        super(message);
    }
}