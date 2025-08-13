package org.bluecollar.bluecollar.admin.controller;

import jakarta.validation.Valid;
import org.bluecollar.bluecollar.admin.dto.*;
import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.model.AdminRole;
import org.bluecollar.bluecollar.admin.service.AdminService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public BlueCollarApiResponse<LoginResponse> adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        String sessionToken = adminService.adminLogin(request);
        return new BlueCollarApiResponse<>(new LoginResponse(sessionToken, "Admin login successful"), 200);

    }

    @PostMapping("/logout")
    public BlueCollarApiResponse<String> adminLogout(@RequestHeader("Admin-Session-Token") String sessionToken) {
        String message = adminService.logout(sessionToken);
        return new BlueCollarApiResponse<>(message, 200);
    }

    @GetMapping("/profile")
    public BlueCollarApiResponse<AdminResponse> getAdminProfile(@RequestHeader("Admin-Session-Token") String sessionToken) {
        AdminResponse response = adminService.getAdminProfile(sessionToken);
        logger.info("Admin profile fetched successfully");
        return new BlueCollarApiResponse<>(response, 200);
    }

    @GetMapping("/list")
    public BlueCollarApiResponse<List<AdminResponse>> getAllAdmins(@RequestHeader("Admin-Session-Token") String sessionToken) {
        List<AdminResponse> response = adminService.getAllAdmins(sessionToken);
        logger.info("All admins fetched successfully");
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PostMapping("/create")
    public BlueCollarApiResponse<AdminResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request,
                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        AdminResponse response = adminService.createAdmin(request, sessionToken);
        logger.info("Admin created successfully with email: {}", request.getEmail());
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PutMapping("/update/{adminId}")
    public BlueCollarApiResponse<AdminResponse> updateAdmin(@PathVariable String adminId,
                                                            @Valid @RequestBody Admin adminUpdate,
                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Updating admin with ID: {}", adminId);
        AdminResponse response = adminService.updateAdmin(adminId, adminUpdate, sessionToken);
        logger.info("Admin updated successfully with ID: {}", adminId);
        return new BlueCollarApiResponse<>(response, 200);

    }

    @PutMapping("/{adminId}/role")
    public BlueCollarApiResponse<AdminResponse> updateAdminRole(@PathVariable String adminId,
                                                                @Valid @RequestBody UpdateAdminRoleRequest request,
                                                                @RequestHeader("Admin-Session-Token") String sessionToken) {
        AdminResponse response = adminService.updateAdminRole(adminId, request, sessionToken);
        logger.info("Admin role updated successfully for ID: {}", adminId);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PutMapping("/{adminId}/activate")
    public BlueCollarApiResponse<String> activateAdmin(@PathVariable String adminId,
                                                       @RequestHeader("Admin-Session-Token") String sessionToken) {
        String message = adminService.activateAdmin(adminId, sessionToken);
        logger.info("Admin activated successfully with ID: {}", adminId);
        return new BlueCollarApiResponse<>(message, 200);
    }

    @PutMapping("/{adminId}/deactivate")
    public BlueCollarApiResponse<String> deactivateAdmin(@PathVariable String adminId,
                                                         @RequestHeader("Admin-Session-Token") String sessionToken) {
        String message = adminService.deactivateAdmin(adminId, sessionToken);
        logger.info("Admin deactivated successfully with ID: {}", adminId);
        return new BlueCollarApiResponse<>(message, 200);
    }

    @DeleteMapping("/{adminId}")
    public BlueCollarApiResponse<String> deleteAdmin(@PathVariable String adminId,
                                                     @RequestHeader("Admin-Session-Token") String sessionToken) {
        String message = adminService.deleteAdmin(adminId, sessionToken);
        logger.info("Admin deleted successfully with ID: {}", adminId);
        return new BlueCollarApiResponse<>(message, 200);
    }

    @GetMapping("/roles")
    public BlueCollarApiResponse<AdminRole[]> getAvailableRoles() {
        AdminRole[] roles = AdminRole.values();
        return new BlueCollarApiResponse<>(roles, 200);
    }
}