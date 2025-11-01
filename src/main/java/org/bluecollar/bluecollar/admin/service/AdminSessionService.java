package org.bluecollar.bluecollar.admin.service;

import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.model.AdminRole;
import org.bluecollar.bluecollar.common.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdminSessionService implements IAdminSessionService {
    
    private final Map<String, AdminSession> sessions = new ConcurrentHashMap<>();
    
    @Override
    public String createSession(Admin admin) {
        String sessionToken = UUID.randomUUID().toString();
        AdminSession session = new AdminSession(admin.getId(), admin.getEmail(), admin.getRole(), LocalDateTime.now());
        sessions.put(sessionToken, session);
        return sessionToken;
    }
    
    @Override
    public AdminSession getSession(String sessionToken) {
        AdminSession session = sessions.get(sessionToken);
        if (session == null) {
            throw new UnauthorizedException("Invalid session token");
        }
        
        // Check if session is expired (24 hours)
        if (session.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
            sessions.remove(sessionToken);
            throw new UnauthorizedException("Session expired");
        }
        
        return session;
    }
    
    @Override
    public void validateSession(String sessionToken) {
        getSession(sessionToken);
    }
    
    @Override
    public void validateRole(String sessionToken, AdminRole requiredRole) {
        AdminSession session = getSession(sessionToken);
        if (session.getRole().ordinal() > requiredRole.ordinal()) {
            throw new UnauthorizedException("Insufficient permissions. Required role: " + requiredRole.getRole());
        }
    }
    
    @Override
    public void validateCanManageUsers(String sessionToken) {
        AdminSession session = getSession(sessionToken);
        if (!session.getRole().canManageUsers()) {
            throw new UnauthorizedException("Insufficient permissions to manage users");
        }
    }
    
    @Override
    public void validateCanManageDeals(String sessionToken) {
        AdminSession session = getSession(sessionToken);
        if (!session.getRole().canManageDeals()) {
            throw new UnauthorizedException("Insufficient permissions to manage deals");
        }
    }
    
    @Override
    public void validateCanCreateAdmins(String sessionToken) {
        AdminSession session = getSession(sessionToken);
        if (!session.getRole().canCreateAdmins()) {
            throw new UnauthorizedException("Insufficient permissions to create admin users");
        }
    }
    
    @Override
    public void validateCanViewDeals(String sessionToken) {
        getSession(sessionToken);
        // All roles can view deals (SUPER_ADMIN, ADMIN, VIEWER)
        // This method just validates the session exists
    }
    
    @Override
    public void logout(String sessionToken) {
        sessions.remove(sessionToken);
    }
    
    public static class AdminSession {
        private final String adminId;
        private final String email;
        private final AdminRole role;
        private final LocalDateTime createdAt;
        
        public AdminSession(String adminId, String email, AdminRole role, LocalDateTime createdAt) {
            this.adminId = adminId;
            this.email = email;
            this.role = role;
            this.createdAt = createdAt;
        }
        
        public String getAdminId() { return adminId; }
        public String getEmail() { return email; }
        public AdminRole getRole() { return role; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
}
