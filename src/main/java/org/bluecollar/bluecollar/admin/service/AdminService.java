package org.bluecollar.bluecollar.admin.service;

import org.bluecollar.bluecollar.admin.dto.*;
import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.model.AdminRole;
import org.bluecollar.bluecollar.admin.model.PasswordResetToken;
import org.bluecollar.bluecollar.admin.repository.AdminRepository;
import org.bluecollar.bluecollar.admin.repository.PasswordResetTokenRepository;
import org.bluecollar.bluecollar.common.exception.BadRequestException;
import org.bluecollar.bluecollar.common.exception.ResourceNotFoundException;
import org.bluecollar.bluecollar.common.exception.UnauthorizedException;
import org.bluecollar.bluecollar.common.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    private final AdminRepository adminRepository;
    private final AdminSessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    
    @Autowired
    public AdminService(AdminRepository adminRepository, AdminSessionService sessionService, 
                       PasswordEncoder passwordEncoder, PasswordResetTokenRepository passwordResetTokenRepository,
                       EmailService emailService) {
        this.adminRepository = adminRepository;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    // Expose session service for controllers needing role-aware lists
    public AdminSessionService getSessionService() {
        return sessionService;
    }
    
    @Transactional
    public String adminLogin(AdminLoginRequest request) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        
        if (adminOpt.isEmpty()) {
            throw new UnauthorizedException("Invalid email or password");
        }
        
        Admin admin = adminOpt.get();
        
        if (!admin.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        
        return sessionService.createSession(admin);
    }
    
    @Transactional
    public AdminResponse createAdmin(CreateAdminRequest request, String sessionToken) {
        // Validate that the current admin can create new admins
        sessionService.validateCanCreateAdmins(sessionToken);
        
        // Check if email already exists
        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }
        
        AdminSessionService.AdminSession creator = sessionService.getSession(sessionToken);
        Admin admin = new Admin();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setName(request.getName());
        // Role assignment rules:
        // - SUPER_ADMIN can create ADMIN or VIEWER (but not another SUPER_ADMIN if one already exists)
        // - ADMIN can create only VIEWER
        if (creator.getRole() == AdminRole.ADMIN) {
            if (request.getRole() != AdminRole.VIEWER) {
                throw new BadRequestException("ADMIN can only create VIEWER");
            }
        }
        if (creator.getRole() == AdminRole.SUPER_ADMIN) {
            if (request.getRole() == AdminRole.SUPER_ADMIN) {
                long superAdminCount = adminRepository.findAll().stream()
                        .filter(a -> a.getRole() == AdminRole.SUPER_ADMIN)
                        .count();
                if (superAdminCount >= 1) {
                    throw new BadRequestException("A SUPER_ADMIN already exists");
                }
            }
        }
        admin.setRole(request.getRole());
        admin.setCreatedBy(creator.getAdminId());
        
        Admin savedAdmin = adminRepository.save(admin);
        return new AdminResponse(savedAdmin);
    }
    
    @Transactional(readOnly = true)
    public List<AdminResponse> getAllAdmins(String sessionToken) {
        // SUPER_ADMIN: see all; ADMIN: see only VIEWERs
        sessionService.validateCanManageUsers(sessionToken);
        AdminSessionService.AdminSession caller = sessionService.getSession(sessionToken);
        AdminRole callerRole = caller.getRole();

        List<Admin> admins = adminRepository.findAll();
        if (callerRole == AdminRole.ADMIN) {
            admins = admins.stream()
                    .filter(a -> a.getRole() == AdminRole.VIEWER)
                    .collect(Collectors.toList());
        }
        return admins.stream().map(AdminResponse::new).collect(Collectors.toList());
    }
    
    @Transactional
    public AdminResponse updateAdminRole(String adminId, UpdateAdminRoleRequest request, String sessionToken) {
        // Validate that the current admin can manage users
        sessionService.validateCanManageUsers(sessionToken);
        // Enforce role update rules: ADMIN cannot assign roles other than VIEWER
        AdminSessionService.AdminSession updater = sessionService.getSession(sessionToken);
        
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }
        
        Admin admin = adminOpt.get();
        if (updater.getRole() == AdminRole.ADMIN && request.getRole() != AdminRole.VIEWER) {
            throw new BadRequestException("ADMIN can only set role to VIEWER");
        }
        if (request.getRole() == AdminRole.SUPER_ADMIN) {
            long superAdminCount = adminRepository.findAll().stream()
                    .filter(a -> a.getRole() == AdminRole.SUPER_ADMIN && !a.getId().equals(adminId))
                    .count();
            if (superAdminCount >= 1) {
                throw new BadRequestException("A SUPER_ADMIN already exists");
            }
        }
        admin.setRole(request.getRole());
        admin.setUpdatedAt(LocalDateTime.now());
        
        Admin savedAdmin = adminRepository.save(admin);
        return new AdminResponse(savedAdmin);
    }
    
    @Transactional
    public AdminResponse updateAdmin(String adminId, UpdateAdminRequest adminUpdate, String sessionToken) {
        // Validate that the current admin can manage users
        sessionService.validateCanManageUsers(sessionToken);
        
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }
        
        Admin admin = adminOpt.get();
        
        // Update fields (password updates optional and without validation)
        if (adminUpdate.getName() != null) {
            admin.setName(adminUpdate.getName());
        }
        if (adminUpdate.getEmail() != null) {
            // Check if email is already taken by another admin
            Optional<Admin> existingAdmin = adminRepository.findByEmail(adminUpdate.getEmail());
            if (existingAdmin.isPresent() && !existingAdmin.get().getId().equals(adminId)) {
                throw new BadRequestException("Email already registered with another admin");
            }
            admin.setEmail(adminUpdate.getEmail());
        }
        // Password updates allowed but optional (no validation)
        if (adminUpdate.getPassword() != null && !adminUpdate.getPassword().trim().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(adminUpdate.getPassword()));
        }
        
        admin.setUpdatedAt(LocalDateTime.now());
        
        Admin savedAdmin = adminRepository.save(admin);
        return new AdminResponse(savedAdmin);
    }
    
    @Transactional
    public String deleteAdmin(String adminId, String sessionToken) {
        // Validate that the current admin can manage users
        sessionService.validateCanManageUsers(sessionToken);

        AdminSessionService.AdminSession caller = sessionService.getSession(sessionToken);
        AdminRole callerRole = caller.getRole();

        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }

        Admin target = adminOpt.get();

        // Admin delete rules:
        // - ADMIN can delete only VIEWERs
        // - ADMIN cannot delete themselves
        if (callerRole == AdminRole.ADMIN) {
            if (caller.getAdminId().equals(adminId)) {
                throw new UnauthorizedException("ADMIN cannot delete self");
            }
            if (target.getRole() != AdminRole.VIEWER) {
                throw new UnauthorizedException("ADMIN can only delete VIEWER");
            }
        }

        // Don't allow deleting the last SUPER_ADMIN
        if (target.getRole() == AdminRole.SUPER_ADMIN) {
            long superAdminCount = adminRepository.findAll().stream()
                    .filter(a -> a.getRole() == AdminRole.SUPER_ADMIN)
                    .count();
            if (superAdminCount <= 1) {
                throw new BadRequestException("Cannot delete the last SUPER_ADMIN");
            }
        }

        adminRepository.delete(target);
        return "Admin deleted successfully";
    }
    
    @Transactional
    public String deactivateAdmin(String adminId, String sessionToken) {
        // Validate that the current admin can manage users
        sessionService.validateCanManageUsers(sessionToken);
        
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }
        
        Admin admin = adminOpt.get();
        admin.setActive(false);
        admin.setUpdatedAt(LocalDateTime.now());
        
        adminRepository.save(admin);
        return "Admin deactivated successfully";
    }
    
    @Transactional
    public String activateAdmin(String adminId, String sessionToken) {
        // Validate that the current admin can manage users
        sessionService.validateCanManageUsers(sessionToken);
        
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }
        
        Admin admin = adminOpt.get();
        admin.setActive(true);
        admin.setUpdatedAt(LocalDateTime.now());
        
        adminRepository.save(admin);
        return "Admin activated successfully";
    }
    
    @Transactional(readOnly = true)
    public AdminResponse getAdminProfile(String sessionToken) {
        AdminSessionService.AdminSession session = sessionService.getSession(sessionToken);
        Optional<Admin> adminOpt = adminRepository.findById(session.getAdminId());
        
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found");
        }
        
        return new AdminResponse(adminOpt.get());
    }
    
    @Transactional
    public String logout(String sessionToken) {
        sessionService.logout(sessionToken);
        return "Logged out successfully";
    }
    
    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with email: " + request.getEmail());
        }
        
        Admin admin = adminOpt.get();
        if (!admin.isActive()) {
            throw new BadRequestException("Account is deactivated");
        }
        
        // Delete any existing tokens for this email
        passwordResetTokenRepository.deleteByEmail(request.getEmail());
        
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));
        
        // Save token
        PasswordResetToken token = new PasswordResetToken(request.getEmail(), otp);
        passwordResetTokenRepository.save(token);
        
        // Send email
        emailService.sendPasswordResetOTP(request.getEmail(), otp);
        
        return "Password reset OTP sent to email";
    }
    
    @Transactional
    public String verifyResetOtp(VerifyResetOtpRequest request) {
        // Find admin by email
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with email: " + request.getEmail());
        }
        
        // Validate OTP
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository
                .findByEmailAndOtpAndUsedFalseAndExpiryTimeAfter(request.getEmail(), request.getOtp(), LocalDateTime.now());
        
        if (tokenOpt.isEmpty()) {
            throw new BadRequestException("Invalid or expired OTP");
        }
        
        PasswordResetToken token = tokenOpt.get();
        
        // Mark token as verified (but not used yet)
        token.setVerified(true);
        passwordResetTokenRepository.save(token);
        
        return "OTP verified successfully";
    }
    
    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        // Find admin by email
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with email: " + request.getEmail());
        }
        
        Admin admin = adminOpt.get();
        
        // Check for verified token
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository
                .findByEmailAndVerifiedTrueAndUsedFalseAndExpiryTimeAfter(request.getEmail(), LocalDateTime.now());
        
        if (tokenOpt.isEmpty()) {
            throw new BadRequestException("OTP not verified or expired. Please verify OTP first.");
        }
        
        PasswordResetToken token = tokenOpt.get();
        
        // Update password
        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        admin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(admin);
        
        // Mark token as used
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
        
        return "Password reset successfully";
    }
    
    // Initialize default SUPER_ADMIN if no admins exist
    @Transactional
    public void initializeDefaultAdmin() {
        if (adminRepository.count() == 0) {
            Admin superAdmin = new Admin();
            superAdmin.setEmail("admin@bluecollar.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123"));
            superAdmin.setName("Super Admin");
            superAdmin.setRole(AdminRole.SUPER_ADMIN);
            superAdmin.setCreatedBy("SYSTEM");
            
            adminRepository.save(superAdmin);
        }
    }
}