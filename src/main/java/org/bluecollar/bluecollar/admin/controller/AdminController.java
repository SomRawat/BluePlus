package org.bluecollar.bluecollar.admin.controller;

import org.bluecollar.bluecollar.admin.dto.*;
import org.bluecollar.bluecollar.admin.model.Admin;
import org.bluecollar.bluecollar.admin.service.AdminService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/login")
    public BlueCollarApiResponse<LoginResponse> adminLogin(@RequestBody AdminLoginRequest request) {
        try {
            String sessionToken = adminService.adminLogin(request);
            return new BlueCollarApiResponse<>(new LoginResponse(sessionToken, "Admin login successful"), 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @GetMapping("/list")
    public BlueCollarApiResponse<List<AdminResponse>> getAllAdmins() {
        return new BlueCollarApiResponse<>(adminService.getAllAdmins(), 200);
    }
    
    @PostMapping("/create")
    public BlueCollarApiResponse<AdminResponse> createAdmin(@RequestBody Admin admin) {
        try {
            AdminResponse response = adminService.createAdmin(admin);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @PutMapping("/update/{id}")
    public BlueCollarApiResponse<AdminResponse> updateAdmin(@PathVariable String id, @RequestBody Admin admin) {
        try {
            AdminResponse response = adminService.updateAdmin(id, admin);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public BlueCollarApiResponse<String> deleteAdmin(@PathVariable String id) {
        try {
            adminService.deleteAdmin(id);
            return new BlueCollarApiResponse<>("Admin deleted successfully", 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(e.getMessage(), 400);
        }
    }
    

}