package org.bluecollar.bluecollar.admin.service;

import org.bluecollar.bluecollar.admin.dto.*;
import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.model.AdminRole;
import org.bluecollar.bluecollar.admin.repository.AdminRepository;
import org.bluecollar.bluecollar.common.exception.BadRequestException;
import org.bluecollar.bluecollar.common.exception.ResourceNotFoundException;
import org.bluecollar.bluecollar.common.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    private final AdminRepository adminRepository;
    private final AdminSessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AdminService(AdminRepository adminRepository, AdminSessionService sessionService, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
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
        
        Admin admin = new Admin();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setName(request.getName());
        admin.setRole(request.getRole());
        admin.setCreatedBy(sessionService.getSession(sessionToken).getAdminId());
        
        Admin savedAdmin = adminRepository.save(admin);
        return new AdminResponse(savedAdmin);
    }
    
    @Transactional(readOnly = true)
    public List<AdminResponse> getAllAdmins(String sessionToken) {
        // Only SUPER_ADMIN can view all admins
        sessionService.validateCanManageUsers(sessionToken);
        
        List<Admin> admins = adminRepository.findAll();
        return admins.stream()
                .map(AdminResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AdminResponse updateAdminRole(String adminId, UpdateAdminRoleRequest request, String sessionToken) {
        // Validate that the current admin can manage users
        sessionService.validateCanManageUsers(sessionToken);
        
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }
        
        Admin admin = adminOpt.get();
        admin.setRole(request.getRole());
        admin.setUpdatedAt(LocalDateTime.now());
        
        Admin savedAdmin = adminRepository.save(admin);
        return new AdminResponse(savedAdmin);
    }
    
    @Transactional
    public AdminResponse updateAdmin(String adminId, Admin adminUpdate, String sessionToken) {
        // Validate that the current admin can manage users
        sessionService.validateCanManageUsers(sessionToken);
        
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }
        
        Admin admin = adminOpt.get();
        
        // Update fields
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
        if (adminUpdate.getPassword() != null) {
            admin.setPassword(passwordEncoder.encode(adminUpdate.getPassword()));
        }
        if (adminUpdate.getRole() != null) {
            admin.setRole(adminUpdate.getRole());
        }
        
        admin.setUpdatedAt(LocalDateTime.now());
        
        Admin savedAdmin = adminRepository.save(admin);
        return new AdminResponse(savedAdmin);
    }
    
    @Transactional
    public String deleteAdmin(String adminId, String sessionToken) {
        // Validate that the current admin can manage users
        sessionService.validateCanManageUsers(sessionToken);
        
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }
        
        Admin admin = adminOpt.get();
        
        // Don't allow deleting the last SUPER_ADMIN
        if (admin.getRole() == AdminRole.SUPER_ADMIN) {
            long superAdminCount = adminRepository.findAll().stream()
                    .filter(a -> a.getRole() == AdminRole.SUPER_ADMIN)
                    .count();
            if (superAdminCount <= 1) {
                throw new BadRequestException("Cannot delete the last SUPER_ADMIN");
            }
        }
        
        adminRepository.delete(admin);
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