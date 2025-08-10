package org.bluecollar.bluecollar.deals.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bluecollar.bluecollar.deals.dto.PLPData;

@Document(collection = "plp_pages")
public class PLP {
    @Id
    private String id;
    private String categoryId;
    private PLPData data;
    private boolean active;

    public PLP() {}

    public PLP(String categoryId, PLPData data, boolean active) {
        this.categoryId = categoryId;
        this.data = data;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public PLPData getData() { return data; }
    public void setData(PLPData data) { this.data = data; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}