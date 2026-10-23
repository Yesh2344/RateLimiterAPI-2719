package com.example.ratelimiter.service;

import com.example.ratelimiter.config.AppConfig;
import com.example.ratelimiter.exception.RateLimitExceededException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service implementing a token‑bucket rate limiter per IP address.
 * <p>
 * The algorithm works as follows:
 * <ul>
 *   <li>Each IP has a bucket with {@code maxTokens = requestsPerMinute + burstCapacity}.</li>
 *   <li>Tokens are refilled at a constant rate of {@code requestsPerMinute / 60} per second.</li>
 *   <li>When a request arrives, a token is consumed. If none are available, a {@link RateLimitExceededException} is thrown.</li>
 * </ul>
 */
@Service
public class RateLimitingService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);

    private final AppConfig appConfig;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitingService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @PostConstruct
    private void init() {
        logger.info("RateLimitingService initialized with {} req/min and {} burst capacity",
                appConfig.getRequestsPerMinute(), appConfig.getBurstCapacity());
    }

    /**
     * Checks the rate limit for the given client IP.
     *
     * @param clientIp the IP address of the caller
     * @throws RateLimitExceededException if the client has exhausted its quota
     */
    public void validateRequest(String clientIp) {
        Bucket bucket = buckets.computeIfAbsent(clientIp, ip -> new Bucket(appConfig));
        synchronized (bucket) {
            bucket.refill();
            if (bucket.tryConsume()) {
                logger.debug("Request allowed for IP {}", clientIp);
            } else {
                logger.warn("Rate limit exceeded for IP {}", clientIp);
                throw new RateLimitExceededException("Too many requests. Please try again later.");
            }
        }
    }

    /**
     * Internal bucket representation for a single IP address.
     */
    private static class Bucket {
        private final int maxTokens;
        private final double refillRatePerSecond; // tokens added per second
        private double tokens;
        private long lastRefillTimestamp; // epoch millis

        Bucket(AppConfig config) {
            this.maxTokens = config.getRequestsPerMinute() + config.getBurstCapacity();
            this.refillRatePerSecond = config.getRequestsPerMinute() / 60.0;
            this.tokens = maxTokens;
            this.lastRefillTimestamp = Instant.now().toEpochMilli();
        }

        /**
         * Refill tokens based on elapsed time.
         */
        void refill() {
            long now = Instant.now().toEpochMilli();
            double secondsSinceLast = (now - lastRefillTimestamp) / 1000.0;
            double tokensToAdd = secondsSinceLast * refillRatePerSecond;
            if (tokensToAdd > 0) {
                tokens = Math.min(maxTokens, tokens + tokensToAdd);
                lastRefillTimestamp = now;
            }
        }

        /**
         * Attempts to consume a single token.
         *
         * @return {@code true} if a token was consumed, {@code false} otherwise
         */
        boolean tryConsume() {
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
    }
}