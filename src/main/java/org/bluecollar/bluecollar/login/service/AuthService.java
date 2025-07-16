package org.bluecollar.bluecollar.login.service;

import org.bluecollar.bluecollar.common.exception.BadRequestException;
import org.bluecollar.bluecollar.common.exception.ResourceNotFoundException;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    private final CustomerRepository customerRepository;
    private final OtpSessionRepository otpSessionRepository;
    private final SessionService sessionService;
    private final GoogleOAuthService googleOAuthService;

    @Autowired
    public AuthService(CustomerRepository customerRepository, OtpSessionRepository otpSessionRepository, SessionService sessionService, GoogleOAuthService googleOAuthService) {
        this.customerRepository = customerRepository;
        this.otpSessionRepository = otpSessionRepository;
        this.sessionService = sessionService;
        this.googleOAuthService = googleOAuthService;
    }

    // Encapsulating OTP logic in a transaction ensures that deleting old OTPs
    // and saving the new one is an atomic operation.
    @Transactional
    public String sendOtp(LoginRequest request) {
        if (!ValidationUtil.isValidMobile(request)) {
            throw new BadRequestException("Invalid mobile number format or unsupported country code.");
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
    
    @Transactional
    public LoginResponse verifyOtp(OtpVerifyRequest request, String clientType) {
        if (!ValidationUtil.isValidMobile(request) || !ValidationUtil.isValidOtp(request.getOtp())) {
            throw new BadRequestException("Invalid mobile number or OTP format.");
        }
        
        Optional<OtpSession> otpSessionOpt = otpSessionRepository.findByMobileAndVerifiedFalse(request.getMobile());
        
        if (otpSessionOpt.isEmpty()) {
            throw new BadRequestException("No active OTP session found. Please request a new OTP.");
        }
        
        OtpSession otpSession = otpSessionOpt.get();
        
        if (!otpSession.getOtp().equals(request.getOtp())) {
            // Note: Consider adding a mechanism to track failed attempts to prevent brute-force attacks.
            throw new BadRequestException("The OTP entered is incorrect.");
        }
        
        if (otpSession.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("The OTP has expired. Please request a new one.");
        }
        
        // Mark OTP as verified
        otpSession.setVerified(true);
        otpSessionRepository.save(otpSession);
        
        Optional<Customer> customerOpt = customerRepository.findByMobile(request.getMobile());
        
        Customer customer;
        boolean isFirstTime;
        
        if (customerOpt.isEmpty()) {
            // New customer
            customer = createNewCustomerFromMobile(request);
            isFirstTime = true;
        } else {
            // Existing customer
            customer = customerOpt.get();
            isFirstTime = isProfileIncomplete(customer);
        }
        
        String sessionToken = sessionService.createSession(customer.getId(), clientType);
        
        return new LoginResponse(sessionToken, isFirstTime, customer.getId(), "Login successful");
    }
    
    public LoginResponse googleAuth(GoogleAuthRequest request, String clientType) {
        if (request.getIdToken() == null || request.getIdToken().trim().isEmpty()) {
            throw new BadRequestException("Google ID token is required.");
        }
        
        try {
            GoogleIdToken.Payload payload = googleOAuthService.verifyToken(request.getIdToken());
            return processGoogleLogin(payload, clientType);
        } catch (Exception e) {
            // Log the original exception e
            throw new BadRequestException("Google authentication failed: Invalid token.");
        }
    }
    
    @Transactional
    protected LoginResponse processGoogleLogin(GoogleIdToken.Payload payload, String clientType) {
        String googleId = payload.getSubject();
        
        if (!payload.getEmailVerified()) {
            throw new BadRequestException("Google email is not verified.");
        }
        
        Optional<Customer> customerOpt = customerRepository.findByGoogleId(googleId);
        boolean isFirstTime = customerOpt.isEmpty();
        
        Customer customer;
        if (isFirstTime) {
            customer = createNewCustomerFromGoogle(payload);
        } else {
            customer = customerOpt.get();
            updateCustomerFromGoogle(customer, payload);
        }
        
        String sessionToken = sessionService.createSession(customer.getId(), clientType);
        return new LoginResponse(sessionToken, isFirstTime, customer.getId(), "Google login successful", 
                               customer.getEmail(), customer.getName(), customer.getProfilePhoto());
    }

    // It's good practice to manage entity updates inside @Transactional methods.
    // Consider using Spring Data JPA Auditing (@CreatedDate, @LastModifiedDate) to automatically manage createdAt/updatedAt fields.
    @Transactional
    public Customer updateProfile(String customerId, UpdateProfileRequest request) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
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
    
    private Customer createNewCustomerFromMobile(OtpVerifyRequest request) {
        Customer customer = new Customer();
        customer.setMobile(request.getMobile());
        if (request.getPhoneCode() != null && !request.getPhoneCode().isEmpty()) {
            customer.setPhoneCode(request.getPhoneCode());
        }
        return customerRepository.save(customer);
    }

    private Customer createNewCustomerFromGoogle(@NonNull GoogleIdToken.Payload payload) {
        Customer customer = new Customer();
        customer.setGoogleId(payload.getSubject());
        customer.setEmail(payload.getEmail());
        customer.setName((String) payload.get("name"));
        customer.setProfilePhoto((String) payload.get("picture"));
        return customerRepository.save(customer);
    }

    private void updateCustomerFromGoogle(@NonNull Customer customer, @NonNull GoogleIdToken.Payload payload) {
        boolean needsUpdate = false;
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String profilePhoto = (String) payload.get("picture");

        if (email != null && !email.equals(customer.getEmail())) {
            customer.setEmail(email);
            needsUpdate = true;
        }
        if (name != null && !name.equals(customer.getName())) {
            customer.setName(name);
            needsUpdate = true;
        }
        if (profilePhoto != null && !profilePhoto.equals(customer.getProfilePhoto())) {
            customer.setProfilePhoto(profilePhoto);
            needsUpdate = true;
        }
        if (needsUpdate) {
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.save(customer);
        }
    }

    private String generateOtp() {
        return SecurityUtil.generateSecureOtp();
    }
    
    private boolean isProfileIncomplete(Customer customer) {
        // Profile is incomplete if essential fields are missing
        return customer.getName() == null || customer.getName().trim().isEmpty() ||
               customer.getEmail() == null || customer.getEmail().trim().isEmpty();
    }

    @Transactional(readOnly = true)
    public Customer getCustomerDetails(String sessionToken) {
        String customerId = sessionService.getCustomerIdFromToken(sessionToken);
        
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        
        return customerOpt.get();
    }
}