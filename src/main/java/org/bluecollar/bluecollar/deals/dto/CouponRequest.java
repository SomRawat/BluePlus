package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;

@Data
public class CouponRequest {
    private String brandId;
    private String city;
    private int expiryDays;
}