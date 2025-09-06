package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "user_coupons")
public class UserCoupon {
    @Id
    private String id;
    
    @Indexed
    private String customerId;
    
    @Indexed
    private String brandId;
    
    private String campaignId;
    private String couponCode;
    private boolean redeemed = false;
    private Date redeemedAt;
    private Date createdAt = new Date();
    private Date expiresAt;

    public UserCoupon(String customerId, String brandId, String campaignId, String couponCode, Date expiresAt) {
        this.customerId = customerId;
        this.brandId = brandId;
        this.campaignId = campaignId;
        this.couponCode = couponCode;
        this.expiresAt = expiresAt;
    }
}