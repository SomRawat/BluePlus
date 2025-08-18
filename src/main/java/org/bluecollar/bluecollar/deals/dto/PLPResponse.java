package org.bluecollar.bluecollar.deals.dto;

import org.bluecollar.bluecollar.deals.model.PLP;
import java.util.List;

public class PLPResponse {
    private String categoryId;
    private String title;
    private List<String> tabs;
    private String activeTab;
    private List<PLP.OfferItem> offers;
    private boolean active;

    public PLPResponse() {}

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

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getTabs() { return tabs; }
    public void setTabs(List<String> tabs) { this.tabs = tabs; }
    public String getActiveTab() { return activeTab; }
    public void setActiveTab(String activeTab) { this.activeTab = activeTab; }
    public List<PLP.OfferItem> getOffers() { return offers; }
    public void setOffers(List<PLP.OfferItem> offers) { this.offers = offers; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}