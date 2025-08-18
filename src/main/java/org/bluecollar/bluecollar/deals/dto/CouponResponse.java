package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CouponResponse {
    private String id;
    private String couponCode;
    private String brandName;
    private String brandId;
    private LocalDateTime expiresAt;
    private boolean redeemed;
    private boolean expired;
    private LocalDateTime redeemedAt;
    private String status; // ACTIVE, REDEEMED, EXPIRED, NONE
    private boolean canGenerate;
    private String message;
    
    // UI Display Fields
    private String displayText;
    private String buttonText;
    private String buttonAction; // GENERATE, REDEEM, NONE
    private boolean showButton;
    private String statusColor; // GREEN, RED, ORANGE, GRAY
    private String expiryText;
}