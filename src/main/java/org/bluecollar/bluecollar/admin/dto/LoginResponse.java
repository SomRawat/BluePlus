package org.bluecollar.bluecollar.admin.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String sessionToken;
    private String message;
}