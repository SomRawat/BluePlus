package org.bluecollar.bluecollar.admin.controller;

import org.bluecollar.bluecollar.admin.service.AdminSessionService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.deals.dto.CouponRequest;
import org.bluecollar.bluecollar.admin.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminCouponController {

    private final CouponService couponService;
    private final AdminSessionService adminSessionService;

    @Autowired
    public AdminCouponController(CouponService couponService, AdminSessionService adminSessionService) {
        this.couponService = couponService;
        this.adminSessionService = adminSessionService;
    }

    @PostMapping("/campaign/create")
    public BlueCollarApiResponse<String> createCouponCampaign(
            @RequestBody CouponRequest request,
            @RequestHeader("Admin-Session-Token") String sessionToken) {
        adminSessionService.validateSession(sessionToken);
        String campaignId = couponService.createCouponCampaign(request);
        return new BlueCollarApiResponse<>("Campaign created with ID: " + campaignId, 200);
    }

    @GetMapping("/campaigns")
    public BlueCollarApiResponse<List<CouponRequest>> getAllCampaigns(
            @RequestHeader("Admin-Session-Token") String sessionToken) {
        adminSessionService.validateSession(sessionToken);
        List<CouponRequest> campaigns = couponService.getAllCampaigns();
        return new BlueCollarApiResponse<>(campaigns, 200);
    }

    @DeleteMapping("/campaign/{campaignId}")
    public BlueCollarApiResponse<String> deleteCampaign(
            @PathVariable String campaignId,
            @RequestHeader("Admin-Session-Token") String sessionToken) {
        adminSessionService.validateSession(sessionToken);
        couponService.deleteCampaign(campaignId);
        return new BlueCollarApiResponse<>("Campaign deleted successfully", 200);
    }
}