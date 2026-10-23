package com.example.ratelimiter.controller;

import com.example.ratelimiter.service.RateLimitingService;
import com.example.ratelimiter.util.IpExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple API exposing a greeting endpoint protected by rate limiting.
 */
@RestController
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    private final RateLimitingService rateLimitingService;
    private final IpExtractor ipExtractor;

    public ApiController(RateLimitingService rateLimitingService, IpExtractor ipExtractor) {
        this.rateLimitingService = rateLimitingService;
        this.ipExtractor = ipExtractor;
    }

    /**
     * GET /api/hello
     *
     * @param request the incoming HTTP request
     * @return a friendly greeting
     */
    @GetMapping("/api/hello")
    public String hello(HttpServletRequest request) {
        String clientIp = ipExtractor.extractClientIp(request);
        logger.info("Received request to /api/hello from IP {}", clientIp);
        rateLimitingService.validateRequest(clientIp);
        return "Hello, World!";
    }
}