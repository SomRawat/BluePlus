package org.bluecollar.bluecollar.login.controller;

import org.bluecollar.bluecollar.login.dto.*;
import org.bluecollar.bluecollar.login.service.AuthService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/send-otp")
    public BlueCollarApiResponse<String> sendOtp(@RequestBody LoginRequest request) {
        try {
            String message = authService.sendOtp(request);
            return new BlueCollarApiResponse<>(message, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(e.getMessage(), 400);
        }
    }
    
    @PostMapping("/verify-otp")
    public BlueCollarApiResponse<LoginResponse> verifyOtp(@RequestBody OtpVerifyRequest request, 
                                      @RequestHeader(value = "Api-Client", defaultValue = "web") String clientType) {
        try {
            LoginResponse response = authService.verifyOtp(request, clientType);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @PostMapping("/google-auth")
    public BlueCollarApiResponse<LoginResponse> googleAuth(@RequestBody GoogleAuthRequest request,
                                       @RequestHeader(value = "Api-Client", defaultValue = "web") String clientType) {
        try {
            LoginResponse response = authService.googleAuth(request, clientType);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    

}