package org.bluecollar.bluecollar.deals.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

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

    public PDP() {
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBannerLink() {
        return bannerLink;
    }

    public void setBannerLink(String bannerLink) {
        this.bannerLink = bannerLink;
    }

    public String getBrandDescription() {
        return brandDescription;
    }

    public void setBrandDescription(String brandDescription) {
        this.brandDescription = brandDescription;
    }

    public String getDiscountText() {
        return discountText;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
    }

    public String getValidTill() {
        return validTill;
    }

    public void setValidTill(String validTill) {
        this.validTill = validTill;
    }

    public List<String> getHowItWorksBullets() {
        return howItWorksBullets;
    }

    public void setHowItWorksBullets(List<String> howItWorksBullets) {
        this.howItWorksBullets = howItWorksBullets;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public List<String> getHowToRedeemBullets() {
        return howToRedeemBullets;
    }

    public void setHowToRedeemBullets(List<String> howToRedeemBullets) {
        this.howToRedeemBullets = howToRedeemBullets;
    }

    public List<String> getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(List<String> termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public List<FAQItem> getFaq() {
        return faq;
    }

    public void setFaq(List<FAQItem> faq) {
        this.faq = faq;
    }

    public String getRedeemLink() {
        return redeemLink;
    }

    public void setRedeemLink(String redeemLink) {
        this.redeemLink = redeemLink;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CouponCampaign getCampaign() {
        return campaign;
    }

    public void setCampaign(CouponCampaign campaign) {
        this.campaign = campaign;
    }

    public static class FAQItem {
        private String id;
        private String question;
        private String answer;

        public FAQItem() {
        }

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

    public static class CouponCampaign {
        private String id;
        private String campaignName;
        private String couponCode;
        private int totalLimit;
        private int expiryDays;
        private boolean active = true;
        private java.util.Date createdAt;
        private java.util.Date expiresAt;
        private Integer remaining;
        private String city;

        public CouponCampaign() {
        }

        public CouponCampaign(String id, String campaignName, String couponCode, int totalLimit, int expiryDays, boolean active, java.util.Date createdAt, java.util.Date expiresAt, Integer remaining, String city) {
            this.id = id;
            this.campaignName = campaignName;
            this.couponCode = couponCode;
            this.totalLimit = totalLimit;
            this.expiryDays = expiryDays;
            this.active = active;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
            this.remaining = remaining;
            this.city = city;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCampaignName() {
            return campaignName;
        }

        public void setCampaignName(String campaignName) {
            this.campaignName = campaignName;
        }

        public String getCouponCode() {
            return couponCode;
        }

        public void setCouponCode(String couponCode) {
            this.couponCode = couponCode;
        }

        public int getTotalLimit() {
            return totalLimit;
        }

        public void setTotalLimit(int totalLimit) {
            this.totalLimit = totalLimit;
        }

        public int getExpiryDays() {
            return expiryDays;
        }

        public void setExpiryDays(int expiryDays) {
            this.expiryDays = expiryDays;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public java.util.Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(java.util.Date createdAt) {
            this.createdAt = createdAt;
        }

        public java.util.Date getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(java.util.Date expiresAt) {
            this.expiresAt = expiresAt;
        }

        public Integer getRemaining() {
            return remaining;
        }

        public void setRemaining(Integer remaining) {
            this.remaining = remaining;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}