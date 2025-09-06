package org.bluecollar.bluecollar.deals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import java.util.Date;

@Data
@NoArgsConstructor
public class Coupon {
    @Id
    private String id;
    private String campaignName;
    private String brandId;
    private String city;
    private String couponCode;
    private String couponImageUrl;
    private int noOfCoupons;
    private Boolean active;
    private Date createdAt;
    private Date expiresAt;

    public Coupon(String id, String campaignName, String brandId, String city,
                  String couponCode, String couponImageUrl, int noOfCoupons, Boolean active,
                  Date createdAt, Date expiresAt) {
        this.id = id;
        this.campaignName = campaignName;
        this.brandId = brandId;
        this.city = city;
        this.couponCode = couponCode;
        this.couponImageUrl = couponImageUrl;
        this.noOfCoupons = noOfCoupons;
        this.active = active;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}