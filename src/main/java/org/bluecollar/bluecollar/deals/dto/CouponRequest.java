package org.bluecollar.bluecollar.deals.dto;

import com.google.api.client.util.DateTime;

public class CouponRequest {
    // When present, treat as update. If null, create new
    private String couponId; // optional explicit key for updates

    // Existing fields
    private String id; // kept for backward compatibility, may also be used as update key
    private Boolean active = Boolean.TRUE;
    private String campaignName;
    private String brandId;
    private String city;
    private String couponCode;
    private int totalLimit;

    // Either provide expiryDays (preferred) or expiryDate
    private Integer expiryDays; // optional; when present, converted to expiryDate
    private DateTime expiryDate; // persisted in Coupon.expiresAt

    public CouponRequest() {
    }

    public CouponRequest(String id, Boolean active, String campaignName, String brandId, String city,
                         String couponCode, int totalLimit, DateTime expiryDate) {
        this.id = id;
        this.active = active;
        this.campaignName = campaignName;
        this.brandId = brandId;
        this.city = city;
        this.couponCode = couponCode;
        this.totalLimit = totalLimit;
        this.expiryDate = expiryDate;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public Integer getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(Integer expiryDays) {
        this.expiryDays = expiryDays;
    }

    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(DateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}