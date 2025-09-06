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
    private String couponImageUrl;
    private int noOfCoupons;
    private Integer expiryDays;
    private String expiryDate;


}