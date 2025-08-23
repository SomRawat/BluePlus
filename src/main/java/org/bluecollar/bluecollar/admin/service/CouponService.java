package org.bluecollar.bluecollar.admin.service;

import com.google.api.client.util.DateTime;
import org.bluecollar.bluecollar.deals.dto.CouponRequest;
import org.bluecollar.bluecollar.deals.model.Coupon;
import org.bluecollar.bluecollar.deals.model.PDP;
import org.bluecollar.bluecollar.deals.repository.CouponRepository;
import org.bluecollar.bluecollar.deals.repository.PDPRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.util.*;

@Service
public class CouponService {

    private final PDPRepository pdpRepository;
    private final CouponRepository couponRepository;

    public CouponService(PDPRepository pdpRepository, CouponRepository couponRepository) {
        this.pdpRepository = pdpRepository;
        this.couponRepository = couponRepository;
    }

    public String createCouponCampaign(CouponRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        // Basic validations
        if (!StringUtils.hasText(request.getBrandId())) {
            throw new IllegalArgumentException("brandId (PDP id) is required");
        }
        if (!StringUtils.hasText(request.getCampaignName())) {
            throw new IllegalArgumentException("campaignName is required");
        }
        if (request.getTotalLimit() <= 0) {
            throw new IllegalArgumentException("totalLimit must be > 0");
        }

        // Compute/validate expiry
        Instant now = Instant.now();
        Instant expiresAt = null;

        // Load PDP
        PDP pdp = pdpRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("PDP page not found for id: " + request.getBrandId()));

        // Enforce single embedded campaign per PDP
        if (pdp.getCampaign() != null) {
            throw new IllegalStateException("PDP already has a campaign. Use an update/replace endpoint if intended.");
        }

        // Prepare campaign
        PDP.CouponCampaign campaign = new PDP.CouponCampaign();
        String campaignId = UUID.randomUUID().toString();
        campaign.setId(campaignId);
        campaign.setCampaignName(request.getCampaignName().trim());
        campaign.setCity(StringUtils.hasText(request.getCity()) ? request.getCity().trim() : null);

        // Handle coupon code: use provided or generate
        String code = StringUtils.hasText(request.getCouponCode())
                ? sanitizeCode(request.getCouponCode())
                : generateCouponCode(pdp.getBrandName());
        campaign.setCouponCode(code);

        if (pdpRepository.existsByCampaign_CouponCodeIgnoreCase(code)) {
            throw new IllegalArgumentException("couponCode already exists: " + code);
        }

        campaign.setTotalLimit(request.getTotalLimit());

        boolean active = request.getActive() == null || request.getActive();
        campaign.setActive(active);

        campaign.setCreatedAt(now);
        campaign.setExpiresAt(expiresAt);

        // Attach to PDP and save
        pdp.setCampaign(campaign);
        pdpRepository.save(pdp);


        // Build and persist the Coupon document
        Coupon coupon = new Coupon();
        coupon.setId(UUID.randomUUID().toString());
        coupon.setBrandId(request.getBrandId());
        coupon.setCampaignName(request.getCampaignName().trim());
        coupon.setCity(StringUtils.hasText(request.getCity()) ? request.getCity().trim() : null);
        coupon.setCouponCode(code);
        coupon.setTotalLimit(request.getTotalLimit());
        coupon.setActive(request.getActive() == null ? true : request.getActive());
        coupon.setCreatedAt(now);
        coupon.setExpiresAt(request.getExpiryDate());

        couponRepository.save(coupon);


        return campaignId;
    }

    // Helpers

    private String sanitizeCode(String code) {
        return code.trim().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    private String generateCouponCode(String brandName) {
        // brand prefix + 6 random alphanumerics
        String prefix = "";
        if (StringUtils.hasText(brandName)) {
            prefix = brandName.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
            prefix = prefix.length() > 4 ? prefix.substring(0, 4) : prefix;
        } else {
            prefix = "BRND";
        }
        String random = randomAlphaNum(6);
        return prefix + "-" + random;
    }

    private String randomAlphaNum(int len) {
        final String alphaNum = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(alphaNum.charAt(r.nextInt(alphaNum.length())));
        }
        return sb.toString();
    }

    // Return all campaigns as DTOs
    public List<CouponRequest> getAllCampaigns() {
        List<Coupon> coupons = couponRepository.findAll();
        List<CouponRequest> result = new ArrayList<>(coupons.size());
        for (Coupon c : coupons) {
            CouponRequest dto = new CouponRequest();
            dto.setId(c.getId()); // Include MongoDB ID
            dto.setActive(c.getActive());
            dto.setCampaignName(c.getCampaignName());
            dto.setBrandId(c.getBrandId());
            dto.setCity(c.getCity());
            dto.setCouponCode(c.getCouponCode());
            dto.setTotalLimit(c.getTotalLimit());
            dto.setExpiryDate(c.getExpiresAt()); // map document expiresAt -> DTO expiryDays
            result.add(dto);
        }
        return result;
    }

    // Delete a campaign by its coupon document id
    public void deleteCampaign(String campaignId) {
        if (!StringUtils.hasText(campaignId)) {
            throw new IllegalArgumentException("campaignId is required");
        }
        if (!couponRepository.existsById(campaignId)) {
            throw new IllegalArgumentException("campaign not found: " + campaignId);
        }
        couponRepository.deleteById(campaignId);
    }

}