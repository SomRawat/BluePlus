package org.bluecollar.bluecollar.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginRequest {
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    public AdminLoginRequest() {}
    
    public AdminLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}