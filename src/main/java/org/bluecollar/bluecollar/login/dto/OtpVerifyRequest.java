package org.bluecollar.bluecollar.login.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class OtpVerifyRequest implements MobileRequest {
    @NotBlank(message = "Mobile number cannot be blank")
    private String mobile;

    private String phoneCode;

    @NotBlank(message = "OTP cannot be blank")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    private String otp;
}