package org.bluecollar.bluecollar.login.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class OtpVerificationResponse {
    private boolean verified;
    private String message;
}