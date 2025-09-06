package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class PLPData {
    private String categoryId;
    private String title;
    private List<String> tabs;
    private String activeTab;
    private List<OfferItem> offers;

    @Data
    public static class OfferItem {
        private String id;
        private String brand;
        private String discount;
        private String discountLabel;
        private String imageUrl;
        private String brandId;
        private boolean active = true;

        public OfferItem() {}

        public OfferItem(String id, String brand, String discount, String discountLabel, String imageUrl) {
            this.id = id;
            this.brand = brand;
            this.discount = discount;
            this.discountLabel = discountLabel;
            this.imageUrl = imageUrl;
        }
    }

    private boolean isActive = true;

    public PLPData() {
        this.tabs = new ArrayList<>();
        this.offers = new ArrayList<>();
    }
}