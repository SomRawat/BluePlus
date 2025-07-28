package org.bluecollar.bluecollar.feedback.service;

import org.bluecollar.bluecollar.feedback.dto.FeedbackRequest;
import org.bluecollar.bluecollar.feedback.model.Feedback;
import org.bluecollar.bluecollar.feedback.repository.FeedbackRepository;
import org.bluecollar.bluecollar.login.model.Customer;
import org.bluecollar.bluecollar.login.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Async
    public void submitFeedback(String sessionToken, FeedbackRequest request) {
        // Get customer details from session token
        Customer customer = authService.getCustomerDetails(sessionToken);
        
        // Create and save feedback
        Feedback feedback = new Feedback();
        feedback.setCustomerId(customer.getId());
        feedback.setCustomerName(customer.getName());
        feedback.setCustomerEmail(customer.getEmail());
        feedback.setMessage(request.getMessage());
        
        feedbackRepository.save(feedback);
        
        // Send email notification
        sendFeedbackEmail(customer, request.getMessage());
    }
    
    private void sendFeedbackEmail(Customer customer, String message) {
        if (mailSender == null) {
            System.out.println("Email not configured. Feedback saved: " + message);
            return;
        }
        
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo("admin@bluecollar.com");
            email.setSubject("New Feedback from " + customer.getName());
            email.setText("Customer: " + customer.getName() + " (" + customer.getEmail() + ")\n\n" +
                         "Feedback: " + message);
            
            mailSender.send(email);
        } catch (Exception e) {
            System.err.println("Failed to send feedback email: " + e.getMessage());
        }
    }
}