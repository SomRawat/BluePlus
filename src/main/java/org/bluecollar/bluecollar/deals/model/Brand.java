package org.bluecollar.bluecollar.deals.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

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
    private boolean active;
    private LocalDateTime createdAt;
    
    public Brand() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getRedirectionLink() { return redirectionLink; }
    public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDiscountText() { return discountText; }
    public void setDiscountText(String discountText) { this.discountText = discountText; }
    
    public LocalDateTime getValidTill() { return validTill; }
    public void setValidTill(LocalDateTime validTill) { this.validTill = validTill; }
    
    public String[] getHowItWorksBullets() { return howItWorksBullets; }
    public void setHowItWorksBullets(String[] howItWorksBullets) { this.howItWorksBullets = howItWorksBullets; }
    
    public String[] getBenefits() { return benefits; }
    public void setBenefits(String[] benefits) { this.benefits = benefits; }
    
    public String[] getHowToRedeemBullets() { return howToRedeemBullets; }
    public void setHowToRedeemBullets(String[] howToRedeemBullets) { this.howToRedeemBullets = howToRedeemBullets; }
    
    public String[] getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String[] termsAndConditions) { this.termsAndConditions = termsAndConditions; }
    
    public FAQ[] getFaq() { return faq; }
    public void setFaq(FAQ[] faq) { this.faq = faq; }
    
    public String getRedeemLink() { return redeemLink; }
    public void setRedeemLink(String redeemLink) { this.redeemLink = redeemLink; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public static class FAQ {
        private String question;
        private String answer;
        
        public FAQ() {}
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
}