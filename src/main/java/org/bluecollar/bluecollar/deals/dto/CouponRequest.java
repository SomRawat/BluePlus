package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;

@Data
public class CouponRequest {
    private String couponId;
    private String id;
    private Boolean active = Boolean.TRUE;
    private String campaignName;
    private String brandId;
    private String city;
    private String couponCode;
    private int noOfCoupons;
    private Integer expiryDays;
    private String expiryDate;

    public CouponRequest(String id, Boolean active, String campaignName, String brandId, String city,
                         String couponCode, int noOfCoupons, String expiryDate) {
        this.id = id;
        this.active = active;
        this.campaignName = campaignName;
        this.brandId = brandId;
        this.city = city;
        this.couponCode = couponCode;
        this.noOfCoupons = noOfCoupons;
        this.expiryDate = expiryDate;
    }
}