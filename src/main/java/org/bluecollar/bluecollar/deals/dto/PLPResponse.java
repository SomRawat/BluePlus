package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bluecollar.bluecollar.deals.model.PLP;
import java.util.List;

@Data
@NoArgsConstructor
public class PLPResponse {
    private String categoryId;
    private String title;
    private List<String> tabs;
    private String activeTab;
    private List<PLP.OfferItem> offers;
    private boolean active;

    public PLPResponse(String categoryId, PLPData data, boolean active) {
        this.categoryId = categoryId;
        this.title = data.getTitle();
        this.tabs = data.getTabs();
        this.activeTab = data.getActiveTab();
        // Convert PLPData.OfferItem to PLP.OfferItem
        this.offers = data.getOffers() != null ? 
            data.getOffers().stream().map(PLP.OfferItem::new).collect(java.util.stream.Collectors.toList()) : 
            null;
        this.active = active;
    }
}