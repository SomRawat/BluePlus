package org.bluecollar.bluecollar.login.dto;

public class OtpVerifyRequest {
    private String mobile;
    private String otp;
    private String phoneCode;

    public OtpVerifyRequest() {}

    public OtpVerifyRequest(String mobile, String otp) {
        this.mobile = mobile;
        this.otp = otp;
    }

    public OtpVerifyRequest(String mobile, String otp, String phoneCode) {
        this.mobile = mobile;
        this.otp = otp;
        this.phoneCode = phoneCode;
    }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getPhoneCode() { return phoneCode; }
    public void setPhoneCode(String phoneCode) { this.phoneCode = phoneCode; }
}