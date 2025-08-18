package org.bluecollar.bluecollar.feedback.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "feedbacks")
public class Feedback {
    
    @Id
    private String id;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();
}