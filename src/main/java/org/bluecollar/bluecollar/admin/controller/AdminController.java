package org.bluecollar.bluecollar.admin.controller;

import jakarta.validation.Valid;
import org.bluecollar.bluecollar.admin.dto.*;
import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.model.AdminRole;
import org.bluecollar.bluecollar.admin.service.AdminService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

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
        return new BlueCollarApiResponse<>(response, 200);
    }

    @GetMapping("/list")
    public BlueCollarApiResponse<List<AdminResponse>> getAllAdmins(@RequestHeader("Admin-Session-Token") String sessionToken) {
        List<AdminResponse> response = adminService.getAllAdmins(sessionToken);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PostMapping("/create")
    public BlueCollarApiResponse<AdminResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request,
                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        AdminResponse response = adminService.createAdmin(request, sessionToken);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PutMapping("/update/{adminId}")
    public BlueCollarApiResponse<AdminResponse> updateAdmin(@PathVariable String adminId,
                                                            @Valid @RequestBody Admin adminUpdate,
                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        AdminResponse response = adminService.updateAdmin(adminId, adminUpdate, sessionToken);
        return new BlueCollarApiResponse<>(response, 200);

    }

    @PutMapping("/{adminId}/role")
    public BlueCollarApiResponse<AdminResponse> updateAdminRole(@PathVariable String adminId,
                                                                @Valid @RequestBody UpdateAdminRoleRequest request,
                                                                @RequestHeader("Admin-Session-Token") String sessionToken) {
        AdminResponse response = adminService.updateAdminRole(adminId, request, sessionToken);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PutMapping("/{adminId}/activate")
    public BlueCollarApiResponse<String> activateAdmin(@PathVariable String adminId,
                                                       @RequestHeader("Admin-Session-Token") String sessionToken) {
        String message = adminService.activateAdmin(adminId, sessionToken);
        return new BlueCollarApiResponse<>(message, 200);
    }

    @PutMapping("/{adminId}/deactivate")
    public BlueCollarApiResponse<String> deactivateAdmin(@PathVariable String adminId,
                                                         @RequestHeader("Admin-Session-Token") String sessionToken) {
        String message = adminService.deactivateAdmin(adminId, sessionToken);
        return new BlueCollarApiResponse<>(message, 200);
    }

    @DeleteMapping("/{adminId}")
    public BlueCollarApiResponse<String> deleteAdmin(@PathVariable String adminId,
                                                     @RequestHeader("Admin-Session-Token") String sessionToken) {
        String message = adminService.deleteAdmin(adminId, sessionToken);
        return new BlueCollarApiResponse<>(message, 200);
    }

    @GetMapping("/roles")
    public BlueCollarApiResponse<AdminRole[]> getAvailableRoles(@RequestHeader("Admin-Session-Token") String sessionToken) {
        var session = adminService.getSessionService().getSession(sessionToken);
        AdminRole callerRole = session.getRole();
        AdminRole[] roles;
        if (callerRole == AdminRole.SUPER_ADMIN) {
            roles = new AdminRole[]{AdminRole.ADMIN, AdminRole.VIEWER};
        } else if (callerRole == AdminRole.ADMIN) {
            roles = new AdminRole[]{AdminRole.VIEWER};
        } else {
            roles = new AdminRole[]{};
        }
        return new BlueCollarApiResponse<>(roles, 200);
    }
    
    @PostMapping("/forgot-password")
    public BlueCollarApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String message = adminService.forgotPassword(request);
        return new BlueCollarApiResponse<>(message, 200);
    }
    
    @PostMapping("/reset-password")
    public BlueCollarApiResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String message = adminService.resetPassword(request);
        return new BlueCollarApiResponse<>(message, 200);
    }
}