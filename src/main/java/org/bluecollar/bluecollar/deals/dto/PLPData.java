package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
public class PLPData {
    private String categoryId;
    private String title;
    private List<String> tabs = new ArrayList<>();
    private String activeTab;
    private List<OfferItem> offers = new ArrayList<>();
    private boolean isActive = true;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfferItem {
        private String id;
        private String brand;
        private String discount;
        private String discountLabel;
        private String imageUrl;
        private boolean active = true;
    }
}