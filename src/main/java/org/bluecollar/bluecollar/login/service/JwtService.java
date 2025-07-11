package org.bluecollar.bluecollar.login.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    
    @Value("${jwt.secret:defaultSecretKey}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;
    
    public String generateToken(String customerId) {
        // Simple JWT implementation - use proper JWT library in production
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"" + customerId + "\",\"exp\":" + (System.currentTimeMillis() + jwtExpiration) + "}";
        
        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        
        return encodedHeader + "." + encodedPayload + ".signature";
    }
    
    public boolean validateToken(String token) {
        // TODO: Implement proper JWT validation
        return token != null && token.split("\\.").length == 3;
    }
    
    public String extractCustomerId(String token) {
        // TODO: Implement proper JWT parsing
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            // Simple extraction - use proper JSON parsing in production
            return payload.split("\"sub\":\"")[1].split("\"")[0];
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }
}