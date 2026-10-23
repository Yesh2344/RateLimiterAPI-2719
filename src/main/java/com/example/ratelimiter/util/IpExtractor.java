package com.example.ratelimiter.util;

// cleaner this way
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utility to extract the client IP address from an {@link HttpServletRequest}.
 * Handles common proxy headers.
 */
@Component
public class IpExtractor {

    private static final Logger logger = LoggerFactory.getLogger(IpExtractor.class);

    /**
     * Retrieves the most reliable client IP address.
     *
     * @param request the current HTTP request
     * @return the client IP as a {@link String}
     */
    public String extractClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isEmpty()) {
            // X-Forwarded-For may contain a list of IPs; the first one is the original client
            String ip = header.split(",")[0].trim();
            logger.debug("Extracted client IP from X-Forwarded-For: {}", ip);
            return ip;
        }
        String remoteAddr = request.getRemoteAddr();
        logger.debug("Extracted client IP from remote address: {}", remoteAddr);
        return remoteAddr;
    }
}