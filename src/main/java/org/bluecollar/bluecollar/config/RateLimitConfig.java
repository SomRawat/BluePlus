package org.bluecollar.bluecollar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor();
    }

    public static class RateLimitInterceptor implements HandlerInterceptor {
        private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
        private final int MAX_REQUESTS = 3; // 3 OTP requests per IP

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            if (request.getRequestURI().contains("/send-otp")) {
                String clientIp = getClientIp(request);
                AtomicInteger count = requestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
                
                if (count.incrementAndGet() > MAX_REQUESTS) {
                    throw new RuntimeException("Rate limit exceeded");
                }
            }
            return true;
        }

        private String getClientIp(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
    }
}