package org.bluecollar.bluecollar.login.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest implements MobileRequest {
    @NotBlank(message = "Mobile number cannot be blank")
    private String mobile;
    
    private String phoneCode;
    
    public LoginRequest(String mobile) {
        this.mobile = mobile;
    }
}