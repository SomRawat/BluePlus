package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "coupons")
public class Coupon {
    @Id
    private String id;
    
    @Indexed
    private String customerId;
    
    @Indexed
    private String brandId;
    
    private String couponCode;
    private String city;
    private LocalDateTime expiresAt;
    private boolean redeemed = false;
    private LocalDateTime redeemedAt;
    private LocalDateTime createdAt = LocalDateTime.now();
}