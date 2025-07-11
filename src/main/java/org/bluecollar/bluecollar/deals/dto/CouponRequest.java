package org.bluecollar.bluecollar.deals.dto;

public class CouponRequest {
    private String brandId;
    private String city;
    private int expiryDays;
    
    public CouponRequest() {}
    
    // Getters and Setters
    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public int getExpiryDays() { return expiryDays; }
    public void setExpiryDays(int expiryDays) { this.expiryDays = expiryDays; }
}