package org.bluecollar.bluecollar.admin.controller;

import org.bluecollar.bluecollar.admin.service.AdminSessionService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.deals.dto.CouponRequest;
import org.bluecollar.bluecollar.admin.service.AdminCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/admin/coupon")
@RestController()
public class AdminCouponController {

    private final AdminCouponService couponService;
    private final AdminSessionService adminSessionService;

    @Autowired
    public AdminCouponController(AdminCouponService couponService, AdminSessionService adminSessionService) {
        this.couponService = couponService;
        this.adminSessionService = adminSessionService;
    }

    @PostMapping("/campaign/create")
    public BlueCollarApiResponse<String> createCouponCampaign(
            @RequestBody CouponRequest request,
            @RequestHeader("Admin-Session-Token") String sessionToken) {
        adminSessionService.validateSession(sessionToken);
        String campaignId = couponService.upsertCouponCampaign(request);
        return new BlueCollarApiResponse<>("Campaign processed with ID: " + campaignId, 200);
    }

    @GetMapping("/campaigns")
    public BlueCollarApiResponse<List<CouponRequest>> getAllCampaigns(
            @RequestHeader("Admin-Session-Token") String sessionToken) {
        adminSessionService.validateSession(sessionToken);
        List<CouponRequest> campaigns = couponService.getAllCampaigns();
        return new BlueCollarApiResponse<>(campaigns, 200);
    }

    // deletefromPdpAlso flag controls whether to remove embedded campaign from PDP as well
    @DeleteMapping("/campaign/{campaignId}")
    public BlueCollarApiResponse<String> deleteCampaign(
            @PathVariable String campaignId,
            @RequestParam(name = "deletefromPdpAlso", defaultValue = "true") boolean deletefromPdpAlso,
            @RequestHeader("Admin-Session-Token") String sessionToken) {
        adminSessionService.validateSession(sessionToken);
        couponService.deleteCampaign(campaignId, deletefromPdpAlso);
        return new BlueCollarApiResponse<>("Campaign deleted successfully", 200);
    }

    @GetMapping("/brand/{brandId}")
    public BlueCollarApiResponse<CouponRequest> getCouponByBrandId(
            @PathVariable String brandId,
            @RequestHeader("Admin-Session-Token") String sessionToken) {
        adminSessionService.validateSession(sessionToken);
        CouponRequest coupon = couponService.getCouponByBrandId(brandId);
        return new BlueCollarApiResponse<>(coupon, 200);
    }
}