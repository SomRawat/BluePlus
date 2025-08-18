package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bluecollar.bluecollar.deals.dto.PDPData;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@Document(collection = "pdp_pages")
public class PDP {
    @Id
    private String id;
    private String categoryId;
    private String brandName;
    private String bannerLink;
    private String brandDescription;
    private String discountText;
    private String validTill;
    private List<String> howItWorksBullets = new ArrayList<>();
    private List<String> benefits = new ArrayList<>();
    private List<String> howToRedeemBullets = new ArrayList<>();
    private List<String> termsAndConditions = new ArrayList<>();
    private List<FAQItem> faq = new ArrayList<>();
    private String redeemLink;
    private boolean redeemed;
    private boolean active = true;
    
    // Coupon fields
    private String activeCampaignId;
    private CouponInfo couponInfo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FAQItem {
        private String id;
        private String question;
        private String answer;
        
        public FAQItem(PDPData.FAQItem dto) {
            this.id = dto.getId();
            this.question = dto.getQuestion();
            this.answer = dto.getAnswer();
        }
    }
    
    @Data
    @NoArgsConstructor
    public static class CouponInfo {
        private String discountText;
        private boolean available;
        private int remainingCount;
    }

    public PDP(String brandId, PDPData data, boolean active) {
        this();
        this.id = data.getId();
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
}