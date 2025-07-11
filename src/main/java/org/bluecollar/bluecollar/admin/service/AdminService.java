package org.bluecollar.bluecollar.admin.service;

import org.bluecollar.bluecollar.admin.dto.*;
import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.repository.AdminRepository;
import org.bluecollar.bluecollar.session.service.SessionService;
import org.bluecollar.bluecollar.common.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public String adminLogin(AdminLoginRequest request) {
        Optional<Admin> adminOpt = adminRepository.findByEmailAndActive(request.getEmail(), true);
        
        if (adminOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), adminOpt.get().getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        return sessionService.createSession(adminOpt.get().getId(), "web");
    }
    
    public List<AdminResponse> getAllAdmins() {
        return adminRepository.findByActive(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public AdminResponse createAdmin(Admin admin) {
        if (!ValidationUtil.isValidEmail(admin.getEmail())) {
            throw new RuntimeException("Invalid email format");
        }
        
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setUpdatedAt(LocalDateTime.now());
        Admin saved = adminRepository.save(admin);
        return convertToResponse(saved);
    }
    
    public AdminResponse updateAdmin(String id, Admin admin) {
        Admin existing = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        existing.setName(admin.getName());
        existing.setRole(admin.getRole());
        existing.setUpdatedAt(LocalDateTime.now());
        
        Admin updated = adminRepository.save(existing);
        return convertToResponse(updated);
    }
    
    public void deleteAdmin(String id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        admin.setActive(false);
        admin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(admin);
    }
    
    private AdminResponse convertToResponse(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setId(admin.getId());
        response.setEmail(admin.getEmail());
        response.setName(admin.getName());
        response.setRole(admin.getRole());
        response.setActive(admin.isActive());
        return response;
    }
}