package org.bluecollar.bluecollar.deals.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CouponResponse {
    private String id;
    private String couponCode;
    private String brandName;
    private String city;
    private LocalDateTime expiresAt;
    private boolean redeemed;
    private LocalDateTime redeemedAt;
    private String message;
    
    public CouponResponse() {}
}