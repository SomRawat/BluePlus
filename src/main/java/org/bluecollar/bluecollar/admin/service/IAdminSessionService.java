package org.bluecollar.bluecollar.admin.service;

import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.model.AdminRole;

public interface IAdminSessionService {
    
    String createSession(Admin admin);
    
    AdminSessionService.AdminSession getSession(String sessionToken);
    
    void validateSession(String sessionToken);
    
    void validateRole(String sessionToken, AdminRole requiredRole);
    
    void validateCanManageUsers(String sessionToken);
    
    void validateCanManageDeals(String sessionToken);
    
    void validateCanCreateAdmins(String sessionToken);
    
    void validateCanViewDeals(String sessionToken);
    
    void logout(String sessionToken);
}

