package org.bluecollar.bluecollar.deals.dto;

public class PLPResponse {
    private String categoryId; // From PLP entity - used for updates/deletes
    private PLPData data;      // The actual data
    private boolean active;    // From PLP entity

    public PLPResponse() {}

    public PLPResponse(String categoryId, PLPData data, boolean active) {
        this.categoryId = categoryId;
        this.data = data;
        this.active = active;
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public PLPData getData() { return data; }
    public void setData(PLPData data) { this.data = data; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}