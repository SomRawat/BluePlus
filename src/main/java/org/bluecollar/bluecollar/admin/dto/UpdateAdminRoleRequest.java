package org.bluecollar.bluecollar.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.bluecollar.bluecollar.admin.model.AdminRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminRoleRequest {
    
    @NotNull(message = "Role is required")
    private AdminRole role;
}
