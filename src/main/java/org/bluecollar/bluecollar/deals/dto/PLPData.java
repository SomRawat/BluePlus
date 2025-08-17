package org.bluecollar.bluecollar.deals.dto;

import java.util.List;
import java.util.ArrayList;

public class PLPData {
    private String categoryId;
    private String title;
    private List<String> tabs;
    private String activeTab;
    private List<OfferItem> offers;

    public static class OfferItem {
        private String id;
        private String brand;
        private String discount;
        private String discountLabel;
        private String imageUrl;

        public OfferItem() {}

        public OfferItem(String id, String brand, String discount, String discountLabel, String imageUrl) {
            this.id = id;
            this.brand = brand;
            this.discount = discount;
            this.discountLabel = discountLabel;
            this.imageUrl = imageUrl;
        }

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

    private boolean isActive = true;

    public PLPData() {
        this.tabs = new ArrayList<>();
        this.offers = new ArrayList<>();
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getTabs() { return tabs; }
    public void setTabs(List<String> tabs) { this.tabs = tabs; }
    public String getActiveTab() { return activeTab; }
    public void setActiveTab(String activeTab) { this.activeTab = activeTab; }
    public List<OfferItem> getOffers() { return offers; }
    public void setOffers(List<OfferItem> offers) { this.offers = offers; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}