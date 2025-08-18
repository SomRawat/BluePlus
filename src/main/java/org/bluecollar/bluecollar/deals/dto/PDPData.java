package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
public class PDPData {
    private String id;
    private String categoryId;
    private String brandName;
    private String bannerLink;
    private String brandDescription;
    private String couponCode;
    private String validTill;
    private List<String> howItWorksBullets = new ArrayList<>();
    private List<String> benefits = new ArrayList<>();
    private List<String> howToRedeemBullets = new ArrayList<>();
    private List<String> termsAndConditions = new ArrayList<>();
    private List<FAQItem> faq = new ArrayList<>();
    private String redeemLink;
    private boolean redeemed;
    private boolean isActive = true;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FAQItem {
        private String id;
        private String question;
        private String answer;
    }
}