package org.bluecollar.bluecollar.login.service;

import org.bluecollar.bluecollar.login.dto.*;
import org.bluecollar.bluecollar.login.model.Customer;
import org.bluecollar.bluecollar.login.model.OtpSession;
import org.bluecollar.bluecollar.login.repository.CustomerRepository;
import org.bluecollar.bluecollar.login.repository.OtpSessionRepository;
import org.bluecollar.bluecollar.session.service.SessionService;
import org.bluecollar.bluecollar.common.util.ValidationUtil;
import org.bluecollar.bluecollar.common.util.SecurityUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private OtpSessionRepository otpSessionRepository;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private GoogleOAuthService googleOAuthService;
    
    public String sendOtp(LoginRequest request) {
        if (!ValidationUtil.isValidMobile(request)) {
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
        if (!ValidationUtil.isValidMobile(request) || !ValidationUtil.isValidOtp(request.getOtp())) {
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
        
        Customer customer;
        boolean isFirstTime;
        
        if (customerOpt.isEmpty()) {
            // New customer
            customer = new Customer();
            customer.setMobile(request.getMobile());
            // Set phone code if provided
            if (request.getPhoneCode() != null && !request.getPhoneCode().isEmpty()) {
                customer.setPhoneCode(request.getPhoneCode());
            }
            try {
                customer = customerRepository.save(customer);
                isFirstTime = true;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create customer: " + e.getMessage());
            }
        } else {
            // Existing customer - check if profile is complete
            customer = customerOpt.get();
            isFirstTime = isProfileIncomplete(customer);
        }
        
        String sessionToken = sessionService.createSession(customer.getId(), clientType);
        
        return new LoginResponse(sessionToken, isFirstTime, customer.getId(), "Login successful");
    }
    
    public LoginResponse googleAuth(GoogleAuthRequest request, String clientType) {
        if (request.getIdToken() == null || request.getIdToken().trim().isEmpty()) {
            throw new RuntimeException("Google ID token is required");
        }
        
        try {
            GoogleIdToken.Payload payload = googleOAuthService.verifyToken(request.getIdToken());
            return processGoogleLogin(payload, clientType);
        } catch (Exception e) {
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }
    
    private LoginResponse processGoogleLogin(GoogleIdToken.Payload payload, String clientType) {
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String profilePhoto = (String) payload.get("picture");
        
        if (!payload.getEmailVerified()) {
            throw new RuntimeException("Google email not verified");
        }
        
        Optional<Customer> customerOpt = customerRepository.findByGoogleId(googleId);
        boolean isFirstTime = customerOpt.isEmpty();
        
        Customer customer;
        if (isFirstTime) {
            customer = new Customer();
            customer.setGoogleId(googleId);
            customer.setEmail(email);
            customer.setName(name);
            customer.setProfilePhoto(profilePhoto);
            customer = customerRepository.save(customer);
        } else {
            customer = customerOpt.get();
            boolean needsUpdate = false;
            
            if (!email.equals(customer.getEmail())) {
                customer.setEmail(email);
                needsUpdate = true;
            }
            if (!name.equals(customer.getName())) {
                customer.setName(name);
                needsUpdate = true;
            }
            if (!profilePhoto.equals(customer.getProfilePhoto())) {
                customer.setProfilePhoto(profilePhoto);
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                customer.setUpdatedAt(LocalDateTime.now());
                customer = customerRepository.save(customer);
            }
        }
        
        String sessionToken = sessionService.createSession(customer.getId(), clientType);
        return new LoginResponse(sessionToken, isFirstTime, customer.getId(), "Google login successful", 
                               customer.getEmail(), customer.getName(), customer.getProfilePhoto());
    }
    
    public Customer updateProfile(String customerId, UpdateProfileRequest request) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        
        if (request.getName() != null) customer.setName(request.getName());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getPhoneCode() != null) customer.setPhoneCode(request.getPhoneCode());
        if (request.getRelationType() != null) customer.setRelationType(request.getRelationType());
        if (request.getDob() != null) customer.setDob(request.getDob());
        if (request.getCountry() != null) customer.setCountry(request.getCountry());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());
        if (request.getCity() != null) customer.setCity(request.getCity());
        if (request.getState() != null) customer.setState(request.getState());
        if (request.getPincode() != null) customer.setPincode(request.getPincode());
        
        customer.setUpdatedAt(LocalDateTime.now());
        return customerRepository.save(customer);
    }
    
    private String generateOtp() {
        return SecurityUtil.generateSecureOtp();
    }
    
    private boolean isProfileIncomplete(Customer customer) {
        // Profile is incomplete if essential fields are missing
        return customer.getName() == null || customer.getName().trim().isEmpty() ||
               customer.getEmail() == null || customer.getEmail().trim().isEmpty();
    }
}