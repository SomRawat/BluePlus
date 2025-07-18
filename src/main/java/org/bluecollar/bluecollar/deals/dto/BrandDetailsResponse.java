package org.bluecollar.bluecollar.deals.dto;

import java.util.List;

public class BrandDetailsResponse {
    private String brandName;
    private String bannerLink;
    private String brandDescription;
    private String discountText;
    private String validTill;
    private List<String> howItWorksBullets;
    private List<String> benefits;
    private List<String> howToRedeemBullets;
    private List<String> termsAndConditions;
    private List<FAQDto> faq;
    private String redeemLink;
    
    public BrandDetailsResponse() {}
    
    // Getters and Setters
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
    
    public List<FAQDto> getFaq() { return faq; }
    public void setFaq(List<FAQDto> faq) { this.faq = faq; }
    
    public String getRedeemLink() { return redeemLink; }
    public void setRedeemLink(String redeemLink) { this.redeemLink = redeemLink; }
    
    public static class FAQDto {
        private String question;
        private String answer;
        
        public FAQDto() {}
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
}