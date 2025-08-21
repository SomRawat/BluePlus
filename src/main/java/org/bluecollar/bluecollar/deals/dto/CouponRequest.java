package org.bluecollar.bluecollar.deals.dto;

import com.google.api.client.util.DateTime;

import java.time.LocalDateTime;

public class CouponRequest {
    private Boolean active = Boolean.TRUE;
    private String campaignName;
    private String brandId;
    private String city;
    private String couponCode;
    private int totalLimit;
    private DateTime expiryDate;

    public CouponRequest() {}

    public CouponRequest(Boolean active, String campaignName, String brandId, String city,
                         String couponCode, int totalLimit, DateTime expiryDate) {
        this.active = active;
        this.campaignName = campaignName;
        this.brandId = brandId;
        this.city = city;
        this.couponCode = couponCode;
        this.totalLimit = totalLimit;
        this.expiryDate = expiryDate;
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

    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(DateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}