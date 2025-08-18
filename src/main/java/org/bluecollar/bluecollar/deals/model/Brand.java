package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "brands")
public class Brand {
    @Id
    private String id;
    private String name;
    private String discount;
    private String imageUrl;
    private String redirectionLink;
    private String description;
    private String discountText;
    private LocalDateTime validTill;
    private String[] howItWorksBullets;
    private String[] benefits;
    private String[] howToRedeemBullets;
    private String[] termsAndConditions;
    private FAQ[] faq;
    private String redeemLink;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Data
    public static class FAQ {
        private String question;
        private String answer;
    }
}