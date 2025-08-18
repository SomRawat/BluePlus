package org.bluecollar.bluecollar.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@bluecollar.com}")
    private String fromEmail;
    
    public void sendPasswordResetOTP(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("BlueCollar Admin - Password Reset OTP");
            message.setText("Your password reset OTP is: " + otp + "\n\nThis OTP will expire in 30 minutes.\n\nIf you did not request this, please ignore this email.");
            message.setFrom(fromEmail);
            
            mailSender.send(message);
            logger.info("Password reset OTP sent successfully to email: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send password reset OTP to email: {}", email, e);
        }
    }
}