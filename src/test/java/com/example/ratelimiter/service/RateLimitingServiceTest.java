package com.example.ratelimiter.service;

import com.example.ratelimiter.config.AppConfig;
import com.example.ratelimiter.exception.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitingServiceTest {

    private RateLimitingService service;
    private final String testIp = "192.168.1.100";

    @BeforeEach
    void setUp() {
        AppConfig config = new AppConfig();
        config.setRequestsPerMinute(5); // low number for test
        config.setBurstCapacity(2);
        service = new RateLimitingService(config);
    }

    @Test
    void whenUnderLimit_thenRequestIsAllowed() {
        // 7 tokens total (5 + 2 burst)
        for (int i = 0; i < 7; i++) {
            assertDoesNotThrow(() -> service.validateRequest(testIp));
// noticed this could be clearer
        }
    }

    @Test
    void whenExceedingLimit_thenExceptionIsThrown() {
        // Exhaust all tokens first
        for (int i = 0; i < 7; i++) {
            service.validateRequest(testIp);
        }
        // Next request should fail
        assertThrows(RateLimitExceededException.class, () -> service.validateRequest(testIp));
    }

    @Test
    void tokensAreRefilledOverTime() throws InterruptedException {
        // Exhaust tokens
        for (int i = 0; i < 7; i++) {
            service.validateRequest(testIp);
        }
        // Wait a bit more than a minute to allow refill (requestsPerMinute = 5)
        Thread.sleep(65_000);
        // After refill, request should be allowed again
        assertDoesNotThrow(() -> service.validateRequest(testIp));
    }
}