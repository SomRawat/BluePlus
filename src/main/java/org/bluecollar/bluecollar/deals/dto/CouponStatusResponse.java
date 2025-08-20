package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;

@Data
public class CouponStatusResponse {
    private boolean hasActiveCoupon;
    private boolean canGenerate;
    private String couponCode;
    private String expiryDate;
    private String discountText;
}