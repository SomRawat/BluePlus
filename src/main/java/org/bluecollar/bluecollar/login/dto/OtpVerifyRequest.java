package org.bluecollar.bluecollar.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OtpVerifyRequest implements MobileRequest {
    @NotBlank(message = "Mobile number cannot be blank")
    private String mobile;

    private String phoneCode;

    @NotBlank(message = "OTP cannot be blank")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    private String otp;

    @Override
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    @Override
    public String getPhoneCode() { return phoneCode; }
    public void setPhoneCode(String phoneCode) { this.phoneCode = phoneCode; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}