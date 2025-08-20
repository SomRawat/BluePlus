package org.bluecollar.bluecollar.login.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "otp_sessions")
public class OtpSession {
    
    @Id
    private String id;
    
    @Indexed
    private String mobile;
    
    private String otp;
    
    @Indexed(expireAfterSeconds = 300)
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
    
    private boolean verified = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public OtpSession(String mobile, String otp) {
        this();
        this.mobile = mobile;
        this.otp = otp;
    }
}