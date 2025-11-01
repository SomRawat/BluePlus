package org.bluecollar.bluecollar.feedback.service;

import org.bluecollar.bluecollar.feedback.dto.FeedbackRequest;

public interface IFeedbackService {
    
    void submitFeedback(String sessionToken, FeedbackRequest request);
}

