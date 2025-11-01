package org.bluecollar.bluecollar.login.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.bluecollar.bluecollar.login.dto.*;
import org.bluecollar.bluecollar.login.model.Customer;

public interface IAuthService {
    
    String sendOtp(LoginRequest request);
    
    LoginResponse verifyOtp(OtpVerifyRequest request, String clientType);
    
    boolean verifyOtpOnly(OtpVerifyRequest request);
    
    LoginResponse googleAuth(GoogleAuthRequest request, String clientType);
    
    Customer updateProfile(String customerId, UpdateProfileRequest request);
    
    Customer getCustomerDetails(String sessionToken);
}

