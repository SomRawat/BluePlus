package org.bluecollar.bluecollar.deals.controller;

import org.bluecollar.bluecollar.deals.dto.*;
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
    
    @PostMapping("/create")
    public BlueCollarApiResponse<CouponResponse> createCoupon(
            @RequestBody CouponRequest request,
            @RequestHeader("Session-Token") String sessionToken) {
        try {
            String customerId = sessionService.validateSession(sessionToken).getCustomerId();
            CouponResponse response = couponService.createCoupon(customerId, request);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @PostMapping("/redeem/{couponCode}")
    public BlueCollarApiResponse<CouponResponse> redeemCoupon(
            @PathVariable String couponCode,
            @RequestHeader("Session-Token") String sessionToken) {
        try {
            String customerId = sessionService.validateSession(sessionToken).getCustomerId();
            CouponResponse response = couponService.redeemCoupon(customerId, couponCode);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @GetMapping("/my-coupons")
    public BlueCollarApiResponse<List<CouponResponse>> getMyCoupons(
            @RequestHeader("Session-Token") String sessionToken) {
        try {
            String customerId = sessionService.validateSession(sessionToken).getCustomerId();
            List<CouponResponse> response = couponService.getUserCoupons(customerId);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @GetMapping("/active")
    public BlueCollarApiResponse<List<CouponResponse>> getActiveCoupons(
            @RequestHeader("Session-Token") String sessionToken) {
        try {
            String customerId = sessionService.validateSession(sessionToken).getCustomerId();
            List<CouponResponse> response = couponService.getActiveCoupons(customerId);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
}