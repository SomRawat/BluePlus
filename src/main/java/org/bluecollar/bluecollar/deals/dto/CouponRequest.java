package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;

@Data
public class CouponRequest {
    private String campaignName;
    private String brandId;
    private String couponCode;
    private int totalLimit; // Total coupons available
    private int expiryDays;
    private boolean isActive;
}