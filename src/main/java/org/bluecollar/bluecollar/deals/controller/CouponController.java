package org.bluecollar.bluecollar.deals.controller;

import org.bluecollar.bluecollar.deals.dto.CouponResponse;
import org.bluecollar.bluecollar.deals.service.CouponService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private SessionService sessionService;
    
    @PostMapping("/generate/{brandId}")
    public BlueCollarApiResponse<CouponResponse> generateCoupon(
            @PathVariable String brandId,
            @RequestHeader("Session-Token") String sessionToken) {
        String customerId = sessionService.validateSession(sessionToken).getCustomerId();
        CouponResponse response = couponService.generateCoupon(customerId, brandId);
        return new BlueCollarApiResponse<>(response, 200);
    }
    
    @PostMapping("/redeem/{couponCode}")
    public BlueCollarApiResponse<String> redeemCoupon(
            @PathVariable String couponCode,
            @RequestHeader("Session-Token") String sessionToken) {
        String customerId = sessionService.validateSession(sessionToken).getCustomerId();
        couponService.redeemCoupon(customerId, couponCode);
        return new BlueCollarApiResponse<>("Coupon redeemed successfully", 200);
    }
    
    @GetMapping("/brand/{brandId}")
    public BlueCollarApiResponse<CouponResponse> getBrandCoupon(
            @PathVariable String brandId,
            @RequestHeader("Session-Token") String sessionToken) {
        String customerId = sessionService.validateSession(sessionToken).getCustomerId();
        CouponResponse response = couponService.getBrandCoupon(customerId, brandId);
        return new BlueCollarApiResponse<>(response, 200);
    }
    
    @GetMapping("/my-coupons")
    public BlueCollarApiResponse<List<CouponResponse>> getMyCoupons(
            @RequestHeader("Session-Token") String sessionToken) {
        String customerId = sessionService.validateSession(sessionToken).getCustomerId();
        List<CouponResponse> response = couponService.getUserCoupons(customerId);
        return new BlueCollarApiResponse<>(response, 200);
    }
}