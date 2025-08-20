package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bluecollar.bluecollar.deals.dto.HomePageData;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "home_pages")
public class HomePage {
    @Id
    private String id;
    private HomePageData data;
    private boolean active;

    public HomePage(HomePageData data, boolean active) {
        this.data = data;
        this.active = active;
    }
}