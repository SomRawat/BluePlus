package org.bluecollar.bluecollar.login.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "otp_sessions")
public class OtpSession {
    
    @Id
    private String id;
    
    @Indexed
    private String mobile;
    
    private String otp;
    
    @Indexed(expireAfterSeconds = 300) // 5 minutes expiry
    private LocalDateTime expiresAt;
    
    private boolean verified;
    private LocalDateTime createdAt;
    
    public OtpSession() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
        this.verified = false;
    }
    
    public OtpSession(String mobile, String otp) {
        this();
        this.mobile = mobile;
        this.otp = otp;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}