package org.bluecollar.bluecollar.feedback.dto;

import jakarta.validation.constraints.NotBlank;

public class FeedbackRequest {
    
    @NotBlank(message = "Message is required")
    private String message;
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}