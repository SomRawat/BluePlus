package org.bluecollar.bluecollar.login.dto;

public class LoginRequest {
    private String mobile;
    private String phoneCode;
    
    public LoginRequest() {}
    
    public LoginRequest(String mobile) {
        this.mobile = mobile;
    }
    
    public LoginRequest(String mobile, String phoneCode) {
        this.mobile = mobile;
        this.phoneCode = phoneCode;
    }
    
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    
    public String getPhoneCode() { return phoneCode; }
    public void setPhoneCode(String phoneCode) { this.phoneCode = phoneCode; }
}