package org.bluecollar.bluecollar.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CHARACTERS = "0123456789";
    
    public static String generateSecureOtp() {
        StringBuilder otp = new StringBuilder(6);
        for (int i = 0; i < 4; i++) {
            otp.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return otp.toString();
    }
    
    public static String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    public static boolean isValidInput(String input, int maxLength) {
        return input != null && 
               input.trim().length() > 0 && 
               input.length() <= maxLength &&
               !containsSqlInjection(input) &&
               !containsXss(input);
    }
    
    private static boolean containsSqlInjection(String input) {
        String[] sqlKeywords = {"'", "\"", ";", "--", "/*", "*/", "xp_", "sp_", "DROP", "DELETE", "INSERT", "UPDATE"};
        String upperInput = input.toUpperCase();
        for (String keyword : sqlKeywords) {
            if (upperInput.contains(keyword.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean containsXss(String input) {
        String[] xssPatterns = {"<script", "</script>", "javascript:", "onload=", "onerror=", "onclick="};
        String lowerInput = input.toLowerCase();
        for (String pattern : xssPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
}