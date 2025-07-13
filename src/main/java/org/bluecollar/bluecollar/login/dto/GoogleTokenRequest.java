package org.bluecollar.bluecollar.login.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleTokenRequest {
    
    @NotBlank(message = "Authorization code is required")
    private String code;
    
    private String redirectUri;
    
    public GoogleTokenRequest() {}
    
    public GoogleTokenRequest(String code, String redirectUri) {
        this.code = code;
        this.redirectUri = redirectUri;
    }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
}