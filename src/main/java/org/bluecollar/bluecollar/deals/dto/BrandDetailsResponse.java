package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import java.util.List;

@Data
public class BrandDetailsResponse {
    private String brandName;
    private String bannerLink;
    private String brandDescription;
    private String couponCode;
    private String validTill;
    private List<String> howItWorksBullets;
    private List<String> benefits;
    private List<String> howToRedeemBullets;
    private List<String> termsAndConditions;
    private List<FAQDto> faq;
    private String redeemLink;
    private boolean redeemed;
    private boolean isActive = true;
    
    // Coupon fields
    private CouponInfo couponInfo;
    
    @Data
    public static class FAQDto {
        private String question;
        private String answer;
    }
    
    @Data
    public static class CouponInfo {
        private String couponCode;
        private boolean available;
        private int remainingCount;
    }
}