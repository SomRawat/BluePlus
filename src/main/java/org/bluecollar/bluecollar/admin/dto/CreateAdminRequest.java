package org.bluecollar.bluecollar.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.bluecollar.bluecollar.admin.model.AdminRole;
import lombok.Data;

@Data
public class CreateAdminRequest {
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotNull(message = "Role is required")
    private AdminRole role;
    
    public CreateAdminRequest() {}
    
    public CreateAdminRequest(String email, String password, String name, AdminRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }
}
