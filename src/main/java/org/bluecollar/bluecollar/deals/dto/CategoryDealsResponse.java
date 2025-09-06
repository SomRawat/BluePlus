package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDealsResponse {
    private String title;
    private List<String> tabs;
    private String activeTab;
    private List<OfferDto> offers;
    private boolean isActive = true;
    

    
    @Data
    public static class OfferDto {
        private String id;
        private String brand;
        private String discount;
        private String discountLabel;
        private String imageUrl;
        private boolean redeemed = false;
    }
}