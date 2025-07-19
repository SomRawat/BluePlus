package org.bluecollar.bluecollar.admin.dto;

public class LoginResponse {
    private String sessionToken;
    private String message;
    
    public LoginResponse(String sessionToken, String message) {
        this.sessionToken = sessionToken;
        this.message = message;
    }
    
    public String getSessionToken() { return sessionToken; }
    public String getMessage() { return message; }
}