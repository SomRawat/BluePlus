package org.bluecollar.bluecollar.deals.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bluecollar.bluecollar.deals.dto.HomePageData;

@Document(collection = "home_pages")
public class HomePage {
    @Id
    private String id;
    private HomePageData data;
    private boolean active;

    public HomePage() {}

    public HomePage(HomePageData data, boolean active) {
        this.data = data;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public HomePageData getData() { return data; }
    public void setData(HomePageData data) { this.data = data; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}