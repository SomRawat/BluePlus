package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
    private List<String> howItWorksBullets;
    private List<String> benefits;
    private List<String> howToRedeemBullets;
    private List<String> termsAndConditions;
    private List<FAQItem> faq;
    private String redeemLink;
    private boolean redeemed;
    private boolean active = true;
    private CouponCampaign campaign;

    public PDP(String id, String categoryId, String brandName, String bannerLink, String brandDescription, String discountText, String validTill, List<String> howItWorksBullets, List<String> benefits, List<String> howToRedeemBullets, List<String> termsAndConditions, List<FAQItem> faq, String redeemLink, boolean redeemed, boolean active, CouponCampaign campaign) {
        this.id = id;
        this.categoryId = categoryId;
        this.brandName = brandName;
        this.bannerLink = bannerLink;
        this.brandDescription = brandDescription;
        this.discountText = discountText;
        this.validTill = validTill;
        this.howItWorksBullets = howItWorksBullets;
        this.benefits = benefits;
        this.howToRedeemBullets = howToRedeemBullets;
        this.termsAndConditions = termsAndConditions;
        this.faq = faq;
        this.redeemLink = redeemLink;
        this.redeemed = redeemed;
        this.active = active;
        this.campaign = campaign;
    }

    @Data
    @NoArgsConstructor
    public static class FAQItem {
        private String id;
        private String question;
        private String answer;

        public FAQItem(String id, String question, String answer) {
            this.id = id;
            this.question = question;
            this.answer = answer;
        }

        public FAQItem(org.bluecollar.bluecollar.deals.dto.PDPData.FAQItem faqItem) {
            this.id = faqItem.getId();
            this.question = faqItem.getQuestion();
            this.answer = faqItem.getAnswer();
        }
    }

    @Data
    @NoArgsConstructor
    public static class CouponCampaign {
        private String id;
        private String campaignName;
        private String couponCode;
        private int noOfCoupons;
        private int expiryDays;
        private boolean active = true;
        private java.util.Date createdAt;
        private java.util.Date expiresAt;
        private Integer remaining;
        private String city;

        public CouponCampaign(String id, String campaignName, String couponCode, int noOfCoupons, int expiryDays, boolean active, java.util.Date createdAt, java.util.Date expiresAt, Integer remaining, String city) {
            this.id = id;
            this.campaignName = campaignName;
            this.couponCode = couponCode;
            this.noOfCoupons = noOfCoupons;
            this.expiryDays = expiryDays;
            this.active = active;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
            this.remaining = remaining;
            this.city = city;
        }
    }
}