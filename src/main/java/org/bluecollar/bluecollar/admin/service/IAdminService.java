package org.bluecollar.bluecollar.admin.service;

import org.bluecollar.bluecollar.admin.dto.*;

import java.util.List;

public interface IAdminService {
    
    String adminLogin(AdminLoginRequest request);
    
    AdminResponse createAdmin(CreateAdminRequest request, String sessionToken);
    
    List<AdminResponse> getAllAdmins(String sessionToken);
    
    AdminResponse updateAdminRole(String adminId, UpdateAdminRoleRequest request, String sessionToken);
    
    AdminResponse updateAdmin(String adminId, UpdateAdminRequest adminUpdate, String sessionToken);
    
    String deleteAdmin(String adminId, String sessionToken);
    
    String deactivateAdmin(String adminId, String sessionToken);
    
    String activateAdmin(String adminId, String sessionToken);
    
    AdminResponse getAdminProfile(String sessionToken);
    
    String logout(String sessionToken);
    
    String forgotPassword(ForgotPasswordRequest request);
    
    String verifyResetOtp(VerifyResetOtpRequest request);
    
    String resetPassword(ResetPasswordRequest request);
    
    void initializeDefaultAdmin();
    
    IAdminSessionService getSessionService();
}

