package org.bluecollar.bluecollar.login.dto;

public class LoginResponse {
    private String token;
    private boolean isFirstTime;
    private String customerId;
    private String message;
    private String email;
    private String name;
    private String profilePhoto;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, boolean isFirstTime, String customerId, String message) {
        this.token = token;
        this.isFirstTime = isFirstTime;
        this.customerId = customerId;
        this.message = message;
    }
    
    public LoginResponse(String token, boolean isFirstTime, String customerId, String message, String email, String name, String profilePhoto) {
        this.token = token;
        this.isFirstTime = isFirstTime;
        this.customerId = customerId;
        this.message = message;
        this.email = email;
        this.name = name;
        this.profilePhoto = profilePhoto;
    }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public boolean isFirstTime() { return isFirstTime; }
    public void setFirstTime(boolean firstTime) { isFirstTime = firstTime; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
}