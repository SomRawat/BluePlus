package org.bluecollar.bluecollar.feedback.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class FeedbackRequest {
    
    @NotBlank(message = "Message is required")
    private String message;
}