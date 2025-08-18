package org.bluecollar.bluecollar.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.model.AdminRole;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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
}