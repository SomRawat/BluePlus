package org.bluecollar.bluecollar.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.bluecollar.bluecollar.admin.model.AdminRole;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UpdateAdminRequest {
    
    @JsonIgnore
    private String id;
    @JsonIgnore
    private AdminRole role;
    @JsonIgnore
    private String roleDescription;
    @JsonIgnore
    private boolean active;
    @JsonIgnore
    private String createdBy;
    @JsonIgnore
    private LocalDateTime createdAt;
    @JsonIgnore
    private LocalDateTime updatedAt;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    // No password validation for updates
    private String password;
    
    public UpdateAdminRequest() {}
}