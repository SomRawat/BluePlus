package org.bluecollar.bluecollar.login.dto;

public class LoginResponse {
    private String token;
    private boolean isFirstTime;
    private String customerId;
    private String message;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, boolean isFirstTime, String customerId, String message) {
        this.token = token;
        this.isFirstTime = isFirstTime;
        this.customerId = customerId;
        this.message = message;
    }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public boolean isFirstTime() { return isFirstTime; }
    public void setFirstTime(boolean firstTime) { isFirstTime = firstTime; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}