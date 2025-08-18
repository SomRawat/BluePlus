package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "banners")
public class Banner {
    @Id
    private String id;
    private String imageUrl;
    private String redirectionLink;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}