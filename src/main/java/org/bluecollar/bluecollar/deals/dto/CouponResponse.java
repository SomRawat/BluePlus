package org.bluecollar.bluecollar.deals.dto;

import java.time.LocalDateTime;

public class CouponResponse {
    private String id;
    private String couponCode;
    private String brandName;
    private String city;
    private LocalDateTime expiresAt;
    private boolean redeemed;
    private LocalDateTime redeemedAt;
    private String message;
    
    public CouponResponse() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public boolean isRedeemed() { return redeemed; }
    public void setRedeemed(boolean redeemed) { this.redeemed = redeemed; }
    
    public LocalDateTime getRedeemedAt() { return redeemedAt; }
    public void setRedeemedAt(LocalDateTime redeemedAt) { this.redeemedAt = redeemedAt; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}