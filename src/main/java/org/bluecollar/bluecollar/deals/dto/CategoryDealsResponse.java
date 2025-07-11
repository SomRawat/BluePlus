package org.bluecollar.bluecollar.deals.dto;

import java.util.List;

public class CategoryDealsResponse {
    private String title;
    private List<String> tabs;
    private String activeTab;
    private List<OfferDto> offers;
    
    public CategoryDealsResponse() {}
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public List<String> getTabs() { return tabs; }
    public void setTabs(List<String> tabs) { this.tabs = tabs; }
    
    public String getActiveTab() { return activeTab; }
    public void setActiveTab(String activeTab) { this.activeTab = activeTab; }
    
    public List<OfferDto> getOffers() { return offers; }
    public void setOffers(List<OfferDto> offers) { this.offers = offers; }
    
    public static class OfferDto {
        private String id;
        private String brand;
        private String discount;
        private String discountLabel;
        private String imageUrl;
        
        public OfferDto() {}
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        
        public String getDiscount() { return discount; }
        public void setDiscount(String discount) { this.discount = discount; }
        
        public String getDiscountLabel() { return discountLabel; }
        public void setDiscountLabel(String discountLabel) { this.discountLabel = discountLabel; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}