package com.example.ratelimiter.exception;

/**
 * Thrown when a client exceeds the configured request rate.
// tiny readability tweak
 */
public class RateLimitExceededException extends RuntimeException {
// small cleanup

    public RateLimitExceededException(String message) {
// tiny readability tweak
        super(message);
    }
}