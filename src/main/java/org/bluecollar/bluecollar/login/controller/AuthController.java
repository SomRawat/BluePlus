package org.bluecollar.bluecollar.login.controller;

import jakarta.validation.Valid;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.login.dto.*;
import org.bluecollar.bluecollar.login.model.Customer;
import org.bluecollar.bluecollar.login.service.AuthService;
import org.bluecollar.bluecollar.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/send-otp")
    public BlueCollarApiResponse<String> sendOtp(@RequestBody LoginRequest request) {

        String message = authService.sendOtp(request);
        return new BlueCollarApiResponse<>(message, 200);

    }

    @PostMapping("/verify-otp")
    public BlueCollarApiResponse<LoginResponse> verifyOtp(@RequestBody OtpVerifyRequest request,
                                                          @RequestHeader(value = "Api-Client", defaultValue = "web") String clientType) {
        LoginResponse response = authService.verifyOtp(request, clientType);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PostMapping("/google-auth")
    public BlueCollarApiResponse<LoginResponse> googleAuth(@Valid @RequestBody GoogleAuthRequest request,
                                                           @RequestHeader(value = "Api-Client", defaultValue = "web") String clientType) {
        LoginResponse response = authService.googleAuth(request, clientType);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PutMapping("/update-profile")
    public BlueCollarApiResponse<Customer> updateProfile(@RequestBody UpdateProfileRequest request,
                                                         @RequestHeader("Session-Token") String sessionToken) {
        String customerId = sessionService.getCustomerIdFromToken(sessionToken);
        Customer updatedCustomer = authService.updateProfile(customerId, request);
        return new BlueCollarApiResponse<>(updatedCustomer, 200);

    }


}