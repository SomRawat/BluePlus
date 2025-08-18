package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "coupon_campaigns")
public class CouponCampaign {
    @Id
    private String id;
    
    private String campaignName;
    
    @Indexed
    private String brandId;
    
    private String discountText;
    private int totalLimit;
    private int usedCount = 0;
    private LocalDateTime expiresAt;
    private boolean isActive = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}