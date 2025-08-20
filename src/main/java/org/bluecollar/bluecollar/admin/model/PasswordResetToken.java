package org.bluecollar.bluecollar.admin.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    private String id;
    private String email;
    private String otp;
    private LocalDateTime expiryTime;
    private boolean used = false;
    private boolean verified = false;

    public PasswordResetToken(String email, String otp) {
        this.email = email;
        this.otp = otp;
        this.expiryTime = LocalDateTime.now().plusMinutes(30);
        this.used = false;
        this.verified = false;
    }
}