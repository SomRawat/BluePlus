package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bluecollar.bluecollar.deals.dto.PLPData;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@Document(collection = "plp_pages")
public class PLP {
    @Id
    private String categoryId;
    private String title;
    private List<String> tabs = new ArrayList<>();
    private String activeTab;
    private List<OfferItem> offers = new ArrayList<>();
    private boolean active;
    
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
        
        public OfferItem(PLPData.OfferItem dto) {
            this.id = dto.getId();
            this.brand = dto.getBrand();
            this.discount = dto.getDiscount();
            this.discountLabel = dto.getDiscountLabel();
            this.imageUrl = dto.getImageUrl();
            this.active = dto.isActive();
        }
    }

    public PLP(String categoryId, PLPData data, boolean active) {
        this.categoryId = categoryId;
        this.title = data.getTitle();
        this.tabs = data.getTabs();
        this.activeTab = data.getActiveTab();
        this.offers = data.getOffers() != null ? 
            data.getOffers().stream().map(OfferItem::new).collect(java.util.stream.Collectors.toList()) : 
            new ArrayList<>();
        this.active = active;
    }
}