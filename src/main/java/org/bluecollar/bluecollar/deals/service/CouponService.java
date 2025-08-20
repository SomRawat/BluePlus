package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.deals.dto.CouponRequest;
import org.bluecollar.bluecollar.deals.dto.CouponResponse;
import org.bluecollar.bluecollar.deals.model.PDP;
import org.bluecollar.bluecollar.deals.model.Coupon;
import org.bluecollar.bluecollar.deals.model.CouponCampaign;
import org.bluecollar.bluecollar.deals.repository.CouponCampaignRepository;
import org.bluecollar.bluecollar.deals.repository.CouponRepository;
import org.bluecollar.bluecollar.deals.repository.PDPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PDPRepository pdpRepository;

    @Autowired
    private CouponCampaignRepository campaignRepository;

    public CouponResponse generateCoupon(String customerId, String brandId) {
        List<CouponCampaign> campaigns = campaignRepository.findByBrandIdAndIsActiveTrue(brandId);
        if (campaigns.isEmpty()) {
            throw new RuntimeException("No active campaigns for this brand");
        }
        
        CouponCampaign campaign = campaigns.get(0);
        
        var existingCoupon = couponRepository.findByCustomerIdAndCampaignIdAndRedeemedFalse(customerId, campaign.getId());
        if (existingCoupon.isPresent()) {
            throw new RuntimeException("You already have an active coupon from this campaign");
        }
        
        int usedCount = couponRepository.countByCampaignIdAndRedeemedTrue(campaign.getId());
        if (usedCount >= campaign.getTotalLimit()) {
            throw new RuntimeException("Coupon limit reached for this campaign");
        }
        
        PDP pdp = pdpRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        
        Coupon coupon = new Coupon();
        coupon.setCustomerId(customerId);
        coupon.setCampaignId(campaign.getId());
        coupon.setCouponCode(generateCouponCode());
        coupon.setExpiresAt(campaign.getExpiresAt());
        
        coupon = couponRepository.save(coupon);
        
        // Update PDP remaining count
        if (pdp.getCouponInfo() != null) {
            int newUsedCount = couponRepository.countByCampaignIdAndRedeemedTrue(campaign.getId());
            int remaining = campaign.getTotalLimit() - newUsedCount;
            pdp.getCouponInfo().setRemainingCount(remaining);
            pdp.getCouponInfo().setAvailable(remaining > 0);
            pdpRepository.save(pdp);
        }
        
        return buildCouponResponse(coupon, pdp, campaign);
    }

    public void redeemCoupon(String customerId, String couponCode) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        if (!coupon.getCustomerId().equals(customerId)) {
            throw new RuntimeException("This coupon does not belong to you");
        }

        if (coupon.isRedeemed()) {
            throw new RuntimeException("Coupon already redeemed");
        }

        if (coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Coupon has expired");
        }

        coupon.setRedeemed(true);
        coupon.setRedeemedAt(LocalDateTime.now());
        couponRepository.save(coupon);
        
        // Update PDP remaining count
        CouponCampaign campaign = campaignRepository.findById(coupon.getCampaignId()).orElse(null);
        if (campaign != null) {
            PDP pdp = pdpRepository.findById(campaign.getBrandId()).orElse(null);
            if (pdp != null && pdp.getCouponInfo() != null) {
                int usedCount = couponRepository.countByCampaignIdAndRedeemedTrue(campaign.getId());
                int remaining = campaign.getTotalLimit() - usedCount;
                pdp.getCouponInfo().setRemainingCount(remaining);
                pdp.getCouponInfo().setAvailable(remaining > 0);
                pdpRepository.save(pdp);
            }
        }
    }

    public CouponResponse getBrandCoupon(String customerId, String brandId) {
        PDP pdp = pdpRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        List<CouponCampaign> campaigns = campaignRepository.findByBrandIdAndIsActiveTrue(brandId);
        if (campaigns.isEmpty()) {
            CouponResponse response = new CouponResponse();
            response.setBrandId(brandId);
            response.setBrandName(pdp.getBrandName());
            response.setStatus("NONE");
            response.setCanGenerate(false);
            response.setMessage("No active campaigns available");
            response.setDisplayText("No Coupons Available");
            response.setShowButton(false);
            response.setStatusColor("GRAY");
            return response;
        }

        CouponCampaign campaign = campaigns.get(0);

        var userCoupon = couponRepository.findByCustomerIdAndCampaignIdAndRedeemedFalse(customerId, campaign.getId());

        if (userCoupon.isPresent()) {
            return buildCouponResponse(userCoupon.get(), pdp, campaign);
        }

        int usedCount = couponRepository.countByCampaignIdAndRedeemedTrue(campaign.getId());
        int remaining = campaign.getTotalLimit() - usedCount;
        
        // Update PDP with real-time count
        if (pdp.getCouponInfo() != null) {
            pdp.getCouponInfo().setRemainingCount(remaining);
            pdp.getCouponInfo().setAvailable(remaining > 0);
            pdpRepository.save(pdp);
        }

        CouponResponse response = new CouponResponse();
        response.setBrandId(brandId);
        response.setBrandName(pdp.getBrandName());
        response.setStatus("NONE");

        if (remaining > 0) {
            response.setCanGenerate(true);
            response.setMessage(remaining + " coupons left");
            response.setDisplayText("Get " + campaign.getCouponCode());
            response.setButtonText("Generate Coupon");
            response.setButtonAction("GENERATE");
            response.setShowButton(true);
            response.setStatusColor("ORANGE");
        } else {
            response.setCanGenerate(false);
            response.setMessage("Coupon limit reached");
            response.setDisplayText("All Coupons Claimed");
            response.setButtonText("Check Later");
            response.setShowButton(false);
            response.setStatusColor("RED");
        }

        return response;
    }

    public List<CouponResponse> getUserCoupons(String customerId) {
        List<Coupon> coupons = couponRepository.findByCustomerId(customerId);

        return coupons.stream().map(coupon -> {
            CouponCampaign campaign = campaignRepository.findById(coupon.getCampaignId()).orElse(null);
            PDP pdp = null;
            if (campaign != null) {
                pdp = pdpRepository.findById(campaign.getBrandId()).orElse(null);
            }
            return buildCouponResponse(coupon, pdp, campaign);
        }).collect(Collectors.toList());
    }

    public String createCouponCampaign(CouponRequest request) {
        PDP pdp = pdpRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        CouponCampaign campaign = new CouponCampaign();
        campaign.setCampaignName(request.getCampaignName());
        campaign.setBrandId(request.getBrandId());
        campaign.setCouponCode(request.getCouponCode());
        campaign.setTotalLimit(request.getTotalLimit());
        campaign.setExpiresAt(LocalDateTime.now().plusDays(request.getExpiryDays() > 0 ? request.getExpiryDays() : 30));
        campaign.setActive(request.isActive());

        campaign = campaignRepository.save(campaign);
        
        // Update PDP with coupon info
        pdp.setActiveCampaignId(campaign.getId());
        PDP.CouponInfo couponInfo = new PDP.CouponInfo();
        couponInfo.setCouponCode(campaign.getCouponCode());
        couponInfo.setAvailable(true);
        couponInfo.setRemainingCount(campaign.getTotalLimit());
        pdp.setCouponInfo(couponInfo);
        pdpRepository.save(pdp);
        
        return campaign.getId();
    }

    public List<Object> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(campaign -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", campaign.getId());
                    result.put("campaignName", campaign.getCampaignName());
                    result.put("brandId", campaign.getBrandId());
                    result.put("couponCode", campaign.getCouponCode());
                    result.put("totalLimit", campaign.getTotalLimit());
                    result.put("usedCount", couponRepository.countByCampaignIdAndRedeemedTrue(campaign.getId()));
                    result.put("remaining", campaign.getTotalLimit() - couponRepository.countByCampaignIdAndRedeemedTrue(campaign.getId()));
                    result.put("expiresAt", campaign.getExpiresAt());
                    result.put("isActive", campaign.isActive());
                    return result;
                })
                .collect(Collectors.toList());
    }
    
    public void deleteCampaign(String campaignId) {
        campaignRepository.deleteById(campaignId);
    }
    
    private CouponResponse buildCouponResponse(Coupon coupon, PDP pdp, CouponCampaign campaign) {
        CouponResponse response = new CouponResponse();
        response.setId(coupon.getId());
        response.setCouponCode(coupon.getCouponCode());
        response.setBrandId(campaign != null ? campaign.getBrandId() : "");
        response.setBrandName(pdp != null ? pdp.getBrandName() : "Unknown");
        response.setExpiresAt(coupon.getExpiresAt());
        response.setRedeemed(coupon.isRedeemed());
        response.setRedeemedAt(coupon.getRedeemedAt());
        
        LocalDateTime now = LocalDateTime.now();
        boolean expired = coupon.getExpiresAt().isBefore(now);
        response.setExpired(expired);
        response.setExpiryText("Expires: " + coupon.getExpiresAt().toLocalDate());
        
        String couponCodeText = campaign != null ? campaign.getCouponCode() : "Discount";
        
        if (coupon.isRedeemed()) {
            response.setStatus("REDEEMED");
            response.setCanGenerate(true);
            response.setMessage("Coupon redeemed successfully");
            response.setDisplayText("Coupon Used");
            response.setButtonText("Generate New Coupon");
            response.setButtonAction("GENERATE");
            response.setShowButton(true);
            response.setStatusColor("GRAY");
        } else if (expired) {
            response.setStatus("EXPIRED");
            response.setCanGenerate(true);
            response.setMessage("Coupon expired. You can generate a new one.");
            response.setDisplayText("Coupon Expired");
            response.setButtonText("Generate New Coupon");
            response.setButtonAction("GENERATE");
            response.setShowButton(true);
            response.setStatusColor("RED");
        } else {
            response.setStatus("ACTIVE");
            response.setCanGenerate(false);
            response.setMessage("Active coupon ready to use");
            response.setDisplayText(couponCodeText + " - Code: " + coupon.getCouponCode());
            response.setButtonText("Use Coupon");
            response.setButtonAction("REDEEM");
            response.setShowButton(true);
            response.setStatusColor("GREEN");
        }
        
        return response;
    }
    
    private String generateCouponCode() {
        return "BC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}