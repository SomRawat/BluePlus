package org.bluecollar.bluecollar.deals.dto;



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

    public CouponRequest() {
    }

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

    public int getNoOfCoupons() {
        return noOfCoupons;
    }

    public void setNoOfCoupons(int noOfCoupons) {
        this.noOfCoupons = noOfCoupons;
    }

    public Integer getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(Integer expiryDays) {
        this.expiryDays = expiryDays;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}