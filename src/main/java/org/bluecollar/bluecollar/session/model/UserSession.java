package org.bluecollar.bluecollar.session.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "user_sessions")
public class UserSession {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String sessionToken;
    
    private String customerId;
    private String currentSection;
    
    private LocalDateTime expiresAt;
    private String clientType;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public UserSession(String sessionToken, String customerId, String clientType) {
        this();
        this.sessionToken = sessionToken;
        this.customerId = customerId;
        this.clientType = clientType;
        setExpiryBasedOnClient(clientType);
    }
    
    private void setExpiryBasedOnClient(String clientType) {
        if ("android".equalsIgnoreCase(clientType) || "ios".equalsIgnoreCase(clientType)) {
            this.expiresAt = LocalDateTime.now().plusDays(30);
        } else {
            this.expiresAt = LocalDateTime.now().plusDays(3);
        }
    }
}