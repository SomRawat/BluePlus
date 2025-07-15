package org.bluecollar.bluecollar.common.util;

import org.bluecollar.bluecollar.login.dto.LoginRequest;
import org.bluecollar.bluecollar.login.dto.OtpVerifyRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class ValidationUtil {

    // Map of country codes to validation rules
    private static final Map<String, MobileValidationRule> COUNTRY_MOBILE_RULES = new HashMap<>();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    static {
        // India (+91): 10 digits starting with 6-9
        MobileValidationRule indiaRule = new MobileValidationRule("^[6-9]\\d{9}$", 10);
        COUNTRY_MOBILE_RULES.put("+91", indiaRule);
        COUNTRY_MOBILE_RULES.put("91", indiaRule);

        // Saudi Arabia (+966): 9 digits starting with 5
        MobileValidationRule saudiRule = new MobileValidationRule("^5\\d{8}$", 9);
        COUNTRY_MOBILE_RULES.put("+966", saudiRule);
        COUNTRY_MOBILE_RULES.put("966", saudiRule);

        // UAE (+971): 9 digits starting with 5 or 2 or 3 or 4 or 6 or 7
        MobileValidationRule uaeRule = new MobileValidationRule("^[5234679]\\d{8}$", 9);
        COUNTRY_MOBILE_RULES.put("+971", uaeRule);
        COUNTRY_MOBILE_RULES.put("971", uaeRule);

        // Kuwait (+965): 8 digits starting with 5 or 6 or 9
        MobileValidationRule kuwaitRule = new MobileValidationRule("^[569]\\d{7}$", 8);
        COUNTRY_MOBILE_RULES.put("+965", kuwaitRule);
        COUNTRY_MOBILE_RULES.put("965", kuwaitRule);

        // Qatar (+974): 8 digits starting with 3 or 5 or 6 or 7
        MobileValidationRule qatarRule = new MobileValidationRule("^[3567]\\d{7}$", 8);
        COUNTRY_MOBILE_RULES.put("+974", qatarRule);
        COUNTRY_MOBILE_RULES.put("974", qatarRule);

        // Oman (+968): 8 digits starting with 7 or 9
        MobileValidationRule omanRule = new MobileValidationRule("^[79]\\d{7}$", 8);
        COUNTRY_MOBILE_RULES.put("+968", omanRule);
        COUNTRY_MOBILE_RULES.put("968", omanRule);

        // Bahrain (+973): 8 digits starting with 3
        MobileValidationRule bahrainRule = new MobileValidationRule("^3\\d{7}$", 8);
        COUNTRY_MOBILE_RULES.put("+973", bahrainRule);
        COUNTRY_MOBILE_RULES.put("973", bahrainRule);
    }

    public static boolean isValidMobile(LoginRequest request) {
        if (request == null || request.getMobile() == null) {
            return false;
        }
        String phoneCode = request.getPhoneCode();
        if (phoneCode == null || phoneCode.isEmpty()) {
            phoneCode = "+91";
        }
        return isValidMobile(request.getMobile(), phoneCode);
    }

    public static boolean isValidMobile(OtpVerifyRequest request) {
        if (request == null || request.getMobile() == null) {
            return false;
        }
        String phoneCode = request.getPhoneCode();
        if (phoneCode == null || phoneCode.isEmpty()) {
            phoneCode = "+91";
        }
        return isValidMobile(request.getMobile(), phoneCode);
    }

    public static boolean isValidMobile(String mobile, String phoneCode) {
        if (mobile == null || phoneCode == null) {
            return false;
        }

        String normalizedCode = normalizePhoneCode(phoneCode);
        MobileValidationRule rule = COUNTRY_MOBILE_RULES.get(normalizedCode);

        if (rule != null) {
            return rule.isValid(mobile);
        }

        // Fall back to India if country code not found
        return COUNTRY_MOBILE_RULES.get("+91").isValid(mobile);
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidOtp(String otp) {
        return otp != null && otp.matches("\\d{4}");
    }

    public static boolean isValidPhoneCode(String phoneCode) {
        if (phoneCode == null) {
            return false;
        }
        String normalized = normalizePhoneCode(phoneCode);
        return COUNTRY_MOBILE_RULES.containsKey(normalized);
    }

    /**
     * Normalize the phone code by adding or removing the '+' prefix.
     */
    private static String normalizePhoneCode(String phoneCode) {
        if (phoneCode == null) {
            return null;
        }
        return phoneCode.startsWith("+") ? phoneCode : "+" + phoneCode;
    }

    /**
     * Utility class to hold mobile number validation rules.
     */
    private static class MobileValidationRule {
        private final Pattern pattern;
        private final int expectedLength;

        public MobileValidationRule(String regex, int expectedLength) {
            this.pattern = Pattern.compile(regex);
            this.expectedLength = expectedLength;
        }

        public boolean isValid(String mobile) {
            return mobile != null
                    && mobile.length() == expectedLength
                    && pattern.matcher(mobile).matches();
        }
    }
}