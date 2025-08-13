package org.bluecollar.bluecollar.admin.dto;

import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.model.AdminRole;
import java.time.LocalDateTime;

public class AdminResponse {
    
    private String id;
    private String email;
    private String name;
    private AdminRole role;
    private String roleDescription;
    private boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public AdminResponse() {}
    
    public AdminResponse(Admin admin) {
        this.id = admin.getId();
        this.email = admin.getEmail();
        this.name = admin.getName();
        this.role = admin.getRole();
        this.roleDescription = admin.getRole() != null ? admin.getRole().getDescription() : null;
        this.active = admin.isActive();
        this.createdBy = admin.getCreatedBy();
        this.createdAt = admin.getCreatedAt();
        this.updatedAt = admin.getUpdatedAt();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public AdminRole getRole() { return role; }
    public void setRole(AdminRole role) { this.role = role; }
    
    public String getRoleDescription() { return roleDescription; }
    public void setRoleDescription(String roleDescription) { this.roleDescription = roleDescription; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}