package org.bluecollar.bluecollar.admin.model;

public enum AdminRole {
    SUPER_ADMIN("SUPER_ADMIN", "Full access to all features including user management"),
    ADMIN("ADMIN", "Can manage deals and limited user actions"),
    VIEWER("VIEWER", "Read-only access to view data");
    
    private final String role;
    private final String description;
    
    AdminRole(String role, String description) {
        this.role = role;
        this.description = description;
    }
    
    public String getRole() {
        return role;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AdminRole fromString(String role) {
        for (AdminRole adminRole : AdminRole.values()) {
            if (adminRole.role.equalsIgnoreCase(role)) {
                return adminRole;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }
    
    public boolean canManageUsers() {
        return this == SUPER_ADMIN || this == ADMIN;
    }
    
    public boolean canManageDeals() {
        return this == SUPER_ADMIN || this == ADMIN;
    }
    
    public boolean canViewReports() {
        return this == SUPER_ADMIN || this == ADMIN;
    }
    
    public boolean canCreateAdmins() {
        return this == SUPER_ADMIN || this == ADMIN;
    }
}
