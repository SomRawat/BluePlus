package org.bluecollar.bluecollar.feedback.controller;

import jakarta.validation.Valid;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.feedback.dto.FeedbackRequest;
import org.bluecollar.bluecollar.feedback.service.IFeedbackService;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    
    private final IFeedbackService feedbackService;
    
    public FeedbackController(IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    @PostMapping("/submit")
    public CompletableFuture<BlueCollarApiResponse<String>> submitFeedback(@Valid @RequestBody FeedbackRequest request,
                                                                           @RequestHeader("Session-Token") String sessionToken) {
        return CompletableFuture.supplyAsync(() -> {
            feedbackService.submitFeedback(sessionToken, request);
            return new BlueCollarApiResponse<>("Feedback submitted successfully", 200);
        });
    }
}