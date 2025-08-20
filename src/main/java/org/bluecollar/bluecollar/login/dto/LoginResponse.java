package org.bluecollar.bluecollar.login.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private boolean isFirstTime;
    private String customerId;
    private String message;
    private String email;
    private String name;
    private String profilePhoto;
    
    public LoginResponse(String token, boolean isFirstTime, String customerId, String message) {
        this.token = token;
        this.isFirstTime = isFirstTime;
        this.customerId = customerId;
        this.message = message;
    }
}