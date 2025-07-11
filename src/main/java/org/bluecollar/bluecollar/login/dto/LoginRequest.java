package org.bluecollar.bluecollar.login.dto;

public class LoginRequest {
    private String mobile;
    
    public LoginRequest() {}
    
    public LoginRequest(String mobile) {
        this.mobile = mobile;
    }
    
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
}