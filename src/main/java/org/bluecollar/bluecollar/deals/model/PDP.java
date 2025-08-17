package org.bluecollar.bluecollar.deals.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bluecollar.bluecollar.deals.dto.PDPData;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "pdp_pages")
public class PDP {
    @Id
    private String id; // MongoDB auto-generates if null
    private String categoryId;
    private String brandName;
    private String bannerLink;
    private String brandDescription;
    private String discountText;
    private String validTill;
    private List<String> howItWorksBullets;
    private List<String> benefits;
    private List<String> howToRedeemBullets;
    private List<String> termsAndConditions;
    private List<FAQItem> faq;
    private String redeemLink;
    private boolean redeemed;
    private boolean active = true;
    
    public static class FAQItem {
        private String id;
        private String question;
        private String answer;

        public FAQItem() {}

        public FAQItem(String id, String question, String answer) {
            this.id = id;
            this.question = question;
            this.answer = answer;
        }
        
        public FAQItem(PDPData.FAQItem dto) {
            this.id = dto.getId();
            this.question = dto.getQuestion();
            this.answer = dto.getAnswer();
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }

    public PDP() {
        this.howItWorksBullets = new ArrayList<>();
        this.benefits = new ArrayList<>();
        this.howToRedeemBullets = new ArrayList<>();
        this.termsAndConditions = new ArrayList<>();
        this.faq = new ArrayList<>();
    }

    public PDP(String brandId, PDPData data, boolean active) {
        this();
        this.id = data.getId(); // null = MongoDB auto-generates
        this.categoryId = data.getCategoryId();
        this.brandName = data.getBrandName();
        this.bannerLink = data.getBannerLink();
        this.brandDescription = data.getBrandDescription();
        this.discountText = data.getDiscountText();
        this.validTill = data.getValidTill();
        this.howItWorksBullets = data.getHowItWorksBullets();
        this.benefits = data.getBenefits();
        this.howToRedeemBullets = data.getHowToRedeemBullets();
        this.termsAndConditions = data.getTermsAndConditions();
        this.faq = data.getFaq() != null ? 
            data.getFaq().stream().map(FAQItem::new).collect(java.util.stream.Collectors.toList()) : 
            new ArrayList<>();
        this.redeemLink = data.getRedeemLink();
        this.redeemed = data.isRedeemed();
        this.active = active;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getBannerLink() { return bannerLink; }
    public void setBannerLink(String bannerLink) { this.bannerLink = bannerLink; }
    public String getBrandDescription() { return brandDescription; }
    public void setBrandDescription(String brandDescription) { this.brandDescription = brandDescription; }
    public String getDiscountText() { return discountText; }
    public void setDiscountText(String discountText) { this.discountText = discountText; }
    public String getValidTill() { return validTill; }
    public void setValidTill(String validTill) { this.validTill = validTill; }
    public List<String> getHowItWorksBullets() { return howItWorksBullets; }
    public void setHowItWorksBullets(List<String> howItWorksBullets) { this.howItWorksBullets = howItWorksBullets; }
    public List<String> getBenefits() { return benefits; }
    public void setBenefits(List<String> benefits) { this.benefits = benefits; }
    public List<String> getHowToRedeemBullets() { return howToRedeemBullets; }
    public void setHowToRedeemBullets(List<String> howToRedeemBullets) { this.howToRedeemBullets = howToRedeemBullets; }
    public List<String> getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(List<String> termsAndConditions) { this.termsAndConditions = termsAndConditions; }
    public List<FAQItem> getFaq() { return faq; }
    public void setFaq(List<FAQItem> faq) { this.faq = faq; }
    public String getRedeemLink() { return redeemLink; }
    public void setRedeemLink(String redeemLink) { this.redeemLink = redeemLink; }
    public boolean isRedeemed() { return redeemed; }
    public void setRedeemed(boolean redeemed) { this.redeemed = redeemed; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}