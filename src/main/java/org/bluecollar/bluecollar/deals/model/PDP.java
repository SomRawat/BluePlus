package org.bluecollar.bluecollar.deals.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bluecollar.bluecollar.deals.dto.PDPData;

@Document(collection = "pdp_pages")
public class PDP {
    @Id
    private String id;
    private String brandId;
    private PDPData data;
    private boolean active;

    public PDP() {}

    public PDP(String brandId, PDPData data, boolean active) {
        this.brandId = brandId;
        this.data = data;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }
    public PDPData getData() { return data; }
    public void setData(PDPData data) { this.data = data; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}