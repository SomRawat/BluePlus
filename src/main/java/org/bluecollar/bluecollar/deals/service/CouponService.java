package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.model.*;
import org.bluecollar.bluecollar.deals.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CouponService {
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private BrandRepository brandRepository;
    
    public CouponResponse createCoupon(String customerId, CouponRequest request) {
        // Check if user already has unredeemed coupon for this brand
        if (couponRepository.findByCustomerIdAndBrandIdAndRedeemedFalse(customerId, request.getBrandId()).isPresent()) {
            throw new RuntimeException("You already have an active coupon for this brand");
        }
        
        Brand brand = brandRepository.findByIdAndActiveTrue(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        
        Coupon coupon = new Coupon();
        coupon.setCustomerId(customerId);
        coupon.setBrandId(request.getBrandId());
        coupon.setCouponCode(generateCouponCode());
        coupon.setCity(request.getCity());
        coupon.setExpiresAt(LocalDateTime.now().plusDays(request.getExpiryDays() > 0 ? request.getExpiryDays() : 30));
        
        coupon = couponRepository.save(coupon);
        
        CouponResponse response = new CouponResponse();
        response.setId(coupon.getId());
        response.setCouponCode(coupon.getCouponCode());
        response.setBrandName(brand.getName());
        response.setCity(coupon.getCity());
        response.setExpiresAt(coupon.getExpiresAt());
        response.setRedeemed(false);
        response.setMessage("Coupon created successfully");
        
        return response;
    }
    
    public CouponResponse redeemCoupon(String customerId, String couponCode) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
        
        if (!coupon.getCustomerId().equals(customerId)) {
            throw new RuntimeException("This coupon does not belong to you");
        }
        
        if (coupon.isRedeemed()) {
            throw new RuntimeException("Coupon already redeemed on " + coupon.getRedeemedAt());
        }
        
        if (coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Coupon has expired");
        }
        
        // Mark as redeemed
        coupon.setRedeemed(true);
        coupon.setRedeemedAt(LocalDateTime.now());
        coupon = couponRepository.save(coupon);
        
        Brand brand = brandRepository.findById(coupon.getBrandId()).orElse(null);
        
        CouponResponse response = new CouponResponse();
        response.setId(coupon.getId());
        response.setCouponCode(coupon.getCouponCode());
        response.setBrandName(brand != null ? brand.getName() : "Unknown");
        response.setCity(coupon.getCity());
        response.setExpiresAt(coupon.getExpiresAt());
        response.setRedeemed(true);
        response.setRedeemedAt(coupon.getRedeemedAt());
        response.setMessage("Coupon redeemed successfully");
        
        return response;
    }
    
    public List<CouponResponse> getUserCoupons(String customerId) {
        List<Coupon> coupons = couponRepository.findByCustomerId(customerId);
        
        return coupons.stream().map(coupon -> {
            Brand brand = brandRepository.findById(coupon.getBrandId()).orElse(null);
            
            CouponResponse response = new CouponResponse();
            response.setId(coupon.getId());
            response.setCouponCode(coupon.getCouponCode());
            response.setBrandName(brand != null ? brand.getName() : "Unknown");
            response.setCity(coupon.getCity());
            response.setExpiresAt(coupon.getExpiresAt());
            response.setRedeemed(coupon.isRedeemed());
            response.setRedeemedAt(coupon.getRedeemedAt());
            
            return response;
        }).collect(Collectors.toList());
    }
    
    public List<CouponResponse> getActiveCoupons(String customerId) {
        List<Coupon> coupons = couponRepository.findByCustomerIdAndRedeemedFalse(customerId);
        
        return coupons.stream()
                .filter(coupon -> coupon.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(coupon -> {
                    Brand brand = brandRepository.findById(coupon.getBrandId()).orElse(null);
                    
                    CouponResponse response = new CouponResponse();
                    response.setId(coupon.getId());
                    response.setCouponCode(coupon.getCouponCode());
                    response.setBrandName(brand != null ? brand.getName() : "Unknown");
                    response.setCity(coupon.getCity());
                    response.setExpiresAt(coupon.getExpiresAt());
                    response.setRedeemed(false);
                    
                    return response;
                }).collect(Collectors.toList());
    }
    
    private String generateCouponCode() {
        return "BC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}