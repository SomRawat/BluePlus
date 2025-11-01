package org.bluecollar.bluecollar.login.controller;

import jakarta.validation.Valid;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.common.service.TranslationService;
import org.bluecollar.bluecollar.login.dto.*;
import org.bluecollar.bluecollar.login.model.Customer;
import org.bluecollar.bluecollar.login.service.AuthService;
import org.bluecollar.bluecollar.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;
    private final TranslationService translationService;

    @Autowired
    public AuthController(AuthService authService, SessionService sessionService, TranslationService translationService) {
        this.authService = authService;
        this.sessionService = sessionService;
        this.translationService = translationService;
    }

    @PostMapping("/send-otp")
    public BlueCollarApiResponse<String> sendOtp(@Valid @RequestBody LoginRequest request) {
        String message = authService.sendOtp(request);
        return new BlueCollarApiResponse<>(message, 200);
    }

    @PostMapping("/verify-otp")
    public BlueCollarApiResponse<LoginResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request,
                                                          @RequestHeader(value = "Api-Client", defaultValue = "web") String clientType) {
        LoginResponse response = authService.verifyOtp(request, clientType);
        return new BlueCollarApiResponse<>(response, 200);
    }
    
    @PostMapping("/verify-mobile")
    public BlueCollarApiResponse<OtpVerificationResponse> verifyMobile(@Valid @RequestBody OtpVerifyRequest request) {
        boolean verified = authService.verifyOtpOnly(request);
        OtpVerificationResponse response = new OtpVerificationResponse(verified, "Mobile verification successful");
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PostMapping("/google-auth")
    public BlueCollarApiResponse<LoginResponse> googleAuth(@Valid @RequestBody GoogleAuthRequest request,
                                                           @RequestHeader(value = "Api-Client", defaultValue = "web") String clientType) {
        LoginResponse response = authService.googleAuth(request, clientType);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PutMapping("/update-profile")
    public BlueCollarApiResponse<Customer> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                         @RequestHeader("Session-Token") String sessionToken) {

        String customerId = sessionService.getCustomerIdFromToken(sessionToken);
        Customer updatedCustomer = authService.updateProfile(customerId, request);
        return new BlueCollarApiResponse<>(updatedCustomer, 200);
    }

    @GetMapping("/profile")
    public BlueCollarApiResponse<Customer> getCustomerProfile(
            @RequestHeader("Session-Token") String sessionToken,
            @RequestHeader(value = "X-Accept-Language", required = false) String acceptLanguage) {
        Customer customer = authService.getCustomerDetails(sessionToken);
        return new BlueCollarApiResponse<>(maybeTranslate(customer, acceptLanguage), 200);
    }
    
    private <T> T maybeTranslate(T body, String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.equals("en")) {
            return body;
        }
        return translationService.translateObject(body, acceptLanguage);
    }
}