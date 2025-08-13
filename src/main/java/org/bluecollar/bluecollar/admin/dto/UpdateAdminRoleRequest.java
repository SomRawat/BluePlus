package org.bluecollar.bluecollar.admin.dto;

import jakarta.validation.constraints.NotNull;
import org.bluecollar.bluecollar.admin.model.AdminRole;

public class UpdateAdminRoleRequest {
    
    @NotNull(message = "Role is required")
    private AdminRole role;
    
    public UpdateAdminRoleRequest() {}
    
    public UpdateAdminRoleRequest(AdminRole role) {
        this.role = role;
    }
    
    public AdminRole getRole() { return role; }
    public void setRole(AdminRole role) { this.role = role; }
}
