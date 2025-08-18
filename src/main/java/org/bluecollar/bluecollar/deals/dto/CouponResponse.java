package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import java.time.LocalDateTime;

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
}