package org.bluecollar.bluecollar.login.dto;

public class OtpVerificationResponse {
    private boolean verified;
    private String message;
    
    public OtpVerificationResponse(boolean verified, String message) {
        this.verified = verified;
        this.message = message;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public String getMessage() {
        return message;
    }
}