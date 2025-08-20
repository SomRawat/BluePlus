package org.bluecollar.bluecollar.login.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequest {
    
    @NotBlank(message = "Google ID token is required")
    private String idToken;
}