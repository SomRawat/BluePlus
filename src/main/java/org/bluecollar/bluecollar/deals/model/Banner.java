package org.bluecollar.bluecollar.deals.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "banners")
public class Banner {
    @Id
    private String id;
    private String imageUrl;
    private String redirectionLink;
    private boolean active;
    private LocalDateTime createdAt;
    
    public Banner() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getRedirectionLink() { return redirectionLink; }
    public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}