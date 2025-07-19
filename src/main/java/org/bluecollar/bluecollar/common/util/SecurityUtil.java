package org.bluecollar.bluecollar.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CHARACTERS = "0123456789";
    
    public static String generateSecureOtp() {
        StringBuilder otp = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            otp.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return otp.toString();
    }
    
    public static String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    // The methods isValidInput, containsSqlInjection, and containsXss have been removed.
    // Blacklist-based validation for SQLi and XSS is insecure and easily bypassed.
    //
    // For SQL Injection: Rely on Spring Data JPA's built-in support for parameterized queries.
    // For XSS: Validate input based on expected format (e.g., using jakarta.validation annotations on DTOs)
    // and ensure the frontend performs context-aware output encoding.
}