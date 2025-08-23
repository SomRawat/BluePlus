package org.bluecollar.bluecollar.deals.model;

import org.springframework.data.annotation.Id;
import java.util.Date;

public class Coupon {
    @Id
    private String id;
    private String campaignName;
    private String brandId;
    private String city;
    private String couponCode;
    private int totalLimit;
    private Boolean active;
    private Date createdAt;
    private Date expiresAt;

    public Coupon() {}

    public Coupon(String id, String campaignName, String brandId, String city,
                  String couponCode, int totalLimit, Boolean active,
                  Date createdAt, Date expiresAt) {
        this.id = id;
        this.campaignName = campaignName;
        this.brandId = brandId;
        this.city = city;
        this.couponCode = couponCode;
        this.totalLimit = totalLimit;
        this.active = active;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(int totalLimit) {
        this.totalLimit = totalLimit;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}