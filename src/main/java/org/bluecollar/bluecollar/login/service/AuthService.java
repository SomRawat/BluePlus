package org.bluecollar.bluecollar.login.service;

import org.bluecollar.bluecollar.login.dto.*;
import org.bluecollar.bluecollar.login.model.Customer;
import org.bluecollar.bluecollar.login.model.OtpSession;
import org.bluecollar.bluecollar.login.repository.CustomerRepository;
import org.bluecollar.bluecollar.login.repository.OtpSessionRepository;
import org.bluecollar.bluecollar.session.service.SessionService;
import org.bluecollar.bluecollar.common.util.ValidationUtil;
import org.bluecollar.bluecollar.common.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private OtpSessionRepository otpSessionRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private SessionService sessionService;
    
    public String sendOtp(LoginRequest request) {
        if (!ValidationUtil.isValidMobile(request.getMobile())) {
            throw new RuntimeException("Invalid mobile number");
        }
        
        String otp = generateOtp();
        
        // Delete existing OTP sessions for this mobile
        otpSessionRepository.deleteByMobile(request.getMobile());
        
        // Create new OTP session
        OtpSession otpSession = new OtpSession(request.getMobile(), otp);
        otpSessionRepository.save(otpSession);
        
        // TODO: Integrate with SMS service to send OTP
        System.out.println("OTP for " + request.getMobile() + ": " + otp);
        
        return "OTP sent successfully";
    }
    
    public LoginResponse verifyOtp(OtpVerifyRequest request, String clientType) {
        if (!ValidationUtil.isValidMobile(request.getMobile()) || !ValidationUtil.isValidOtp(request.getOtp())) {
            throw new RuntimeException("Invalid input data");
        }
        
        Optional<OtpSession> otpSessionOpt = otpSessionRepository.findByMobileAndVerifiedFalse(request.getMobile());
        
        if (otpSessionOpt.isEmpty()) {
            throw new RuntimeException("Invalid or expired OTP session");
        }
        
        OtpSession otpSession = otpSessionOpt.get();
        
        if (!otpSession.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }
        
        if (otpSession.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        
        // Mark OTP as verified
        otpSession.setVerified(true);
        otpSessionRepository.save(otpSession);
        
        // Check if customer exists
        Optional<Customer> customerOpt = customerRepository.findByMobile(request.getMobile());
        boolean isFirstTime = customerOpt.isEmpty();
        
        Customer customer;
        if (isFirstTime) {
            customer = new Customer();
            customer.setMobile(request.getMobile());
            customer = customerRepository.save(customer);
        } else {
            customer = customerOpt.get();
        }
        
        String token = jwtService.generateToken(customer.getId());
        String sessionToken = sessionService.createSession(customer.getId(), clientType);
        
        return new LoginResponse(sessionToken, isFirstTime, customer.getId(), "Login successful");
    }
    
    public LoginResponse googleAuth(GoogleAuthRequest request, String clientType) {
        // TODO: Verify Google ID token
        String googleId = extractGoogleId(request.getIdToken());
        String email = extractEmail(request.getIdToken());
        
        Optional<Customer> customerOpt = customerRepository.findByGoogleId(googleId);
        boolean isFirstTime = customerOpt.isEmpty();
        
        Customer customer;
        if (isFirstTime) {
            customer = new Customer();
            customer.setGoogleId(googleId);
            customer.setEmail(email);
            customer = customerRepository.save(customer);
        } else {
            customer = customerOpt.get();
        }
        
        String token = jwtService.generateToken(customer.getId());
        String sessionToken = sessionService.createSession(customer.getId(), clientType);
        
        return new LoginResponse(sessionToken, isFirstTime, customer.getId(), "Google login successful");
    }
    
    private String generateOtp() {
        return SecurityUtil.generateSecureOtp();
    }
    
    private String extractGoogleId(String idToken) {
        // TODO: Implement Google ID token verification
        return "google_id_placeholder";
    }
    
    private String extractEmail(String idToken) {
        // TODO: Extract email from Google ID token
        return "email@example.com";
    }
}