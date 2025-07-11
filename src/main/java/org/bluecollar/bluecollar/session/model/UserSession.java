package org.bluecollar.bluecollar.session.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    
    public UserSession() {
        this.createdAt = LocalDateTime.now();
    }
    
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
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getCurrentSection() { return currentSection; }
    public void setCurrentSection(String currentSection) { this.currentSection = currentSection; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getClientType() { return clientType; }
    public void setClientType(String clientType) { this.clientType = clientType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}