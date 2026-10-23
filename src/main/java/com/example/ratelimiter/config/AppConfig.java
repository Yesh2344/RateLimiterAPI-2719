package com.example.ratelimiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds application rate‑limit properties from {@code application.yml} or environment variables.
 */
@Configuration
@ConfigurationProperties(prefix = "app.rate-limit")
public class AppConfig {

    /**
     * Number of allowed requests per minute per IP.
     */
    private int requestsPerMinute = 60;

    /**
     * Additional burst capacity (tokens) that can be consumed instantly.
     */
    private int burstCapacity = 10;

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public int getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(int burstCapacity) {
        this.burstCapacity = burstCapacity;
    }
}