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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CouponService {

    private final PDPRepository pdpRepository;
    private final CouponRepository couponRepository;

    public CouponService(PDPRepository pdpRepository, CouponRepository couponRepository) {
        this.pdpRepository = pdpRepository;
        this.couponRepository = couponRepository;
    }

    // Create or Update coupon campaign based on presence of couponId / id
    public String upsertCouponCampaign(CouponRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        String effectiveCouponId = StringUtils.hasText(request.getCouponId()) ? request.getCouponId() : request.getId();
        boolean isUpdate = StringUtils.hasText(effectiveCouponId);

        if (!StringUtils.hasText(request.getBrandId())) {
            throw new IllegalArgumentException("brandId (PDP id) is required");
        }
        if (!StringUtils.hasText(request.getCampaignName())) {
            throw new IllegalArgumentException("campaignName is required");
        }
        if (request.getTotalLimit() <= 0) {
            throw new IllegalArgumentException("totalLimit must be > 0");
        }
        if (request.getExpiryDays() == null || request.getExpiryDays() <= 0) {
            throw new IllegalArgumentException("expiryDays must be provided and > 0");
        }

        // Always compute expiryDate from expiryDays at end-of-day UTC
        Instant target = LocalDate.now(ZoneOffset.UTC)
                .plusDays(request.getExpiryDays().longValue())
                .atTime(23, 59, 59)
                .toInstant(ZoneOffset.UTC);
        request.setExpiryDate(new DateTime(target.toEpochMilli()));

        Instant now = Instant.now();

        // Load PDP
        PDP pdp = pdpRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("PDP page not found for id: " + request.getBrandId()));

        // Prepare or update embedded campaign in PDP
        PDP.CouponCampaign campaign = pdp.getCampaign();
        if (isUpdate) {
            if (campaign == null) {
                campaign = new PDP.CouponCampaign();
                campaign.setId(UUID.randomUUID().toString());
                campaign.setCreatedAt(now);
            }
        } else {
            // If PDP already has a campaign and UI didn't pass couponId, treat as update instead of error
            if (campaign != null) {
                isUpdate = true;
            } else {
                campaign = new PDP.CouponCampaign();
                campaign.setId(UUID.randomUUID().toString());
                campaign.setCreatedAt(now);
            }
        }

        campaign.setCampaignName(request.getCampaignName().trim());
        campaign.setCity(StringUtils.hasText(request.getCity()) ? request.getCity().trim() : null);

        // Handle coupon code: use provided or generate
        String code = StringUtils.hasText(request.getCouponCode())
                ? sanitizeCode(request.getCouponCode())
                : generateCouponCode(pdp.getBrandName());
        // If creating OR code changed, ensure uniqueness for other PDPs
        boolean codeChangedOrCreate = (!isUpdate) || (pdp.getCampaign() == null || !code.equalsIgnoreCase(pdp.getCampaign().getCouponCode()));
        if (codeChangedOrCreate && pdpRepository.existsByCampaign_CouponCodeIgnoreCase(code)) {
            // Allow same code if belongs to this brand+same coupon document
            Optional<Coupon> existingSameBrand = couponRepository.findByBrandIdAndCouponCodeIgnoreCase(request.getBrandId(), code);
            if (existingSameBrand.isEmpty() || (isUpdate && !existingSameBrand.get().getId().equals(effectiveCouponId))) {
                throw new IllegalArgumentException("couponCode already exists: " + code);
            }
        }
        campaign.setCouponCode(code);

        campaign.setTotalLimit(request.getTotalLimit());
        campaign.setActive(request.getActive() == null || request.getActive());
        campaign.setExpiryDays(request.getExpiryDays());
        campaign.setExpiresAt(Instant.ofEpochMilli(request.getExpiryDate().getValue()));

        // Attach to PDP and save
        pdp.setCampaign(campaign);
        pdpRepository.save(pdp);

        // Build or update the Coupon document
        Coupon coupon;
        if (isUpdate) {
            if (StringUtils.hasText(effectiveCouponId)) {
                coupon = couponRepository.findById(effectiveCouponId)
                        .orElseThrow(() -> new IllegalArgumentException("campaign not found: " + effectiveCouponId));
            } else {
                // No couponId passed but PDP had a campaign: update the brand's existing coupon if present
                coupon = couponRepository.findFirstByBrandId(request.getBrandId())
                        .orElseGet(() -> {
                            Coupon c = new Coupon();
                            c.setId(UUID.randomUUID().toString());
                            c.setCreatedAt(now);
                            return c;
                        });
            }
        } else {
            coupon = new Coupon();
            coupon.setId(UUID.randomUUID().toString());
            coupon.setCreatedAt(now);
        }
        coupon.setBrandId(request.getBrandId());
        coupon.setCampaignName(request.getCampaignName().trim());
        coupon.setCity(StringUtils.hasText(request.getCity()) ? request.getCity().trim() : null);
        coupon.setCouponCode(code);
        coupon.setTotalLimit(request.getTotalLimit());
        coupon.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        coupon.setExpiresAt(Instant.ofEpochMilli(request.getExpiryDate().getValue()));

        couponRepository.save(coupon);

        return campaign.getId();
    }

    // Return all campaigns as DTOs, ensuring expiryDays is also present for UI
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
            if (c.getExpiresAt() != null) {
                Instant now = Instant.now();
                long days = ChronoUnit.DAYS.between(now.truncatedTo(ChronoUnit.DAYS), c.getExpiresAt());
                dto.setExpiryDays((int) Math.max(days, 0));
                dto.setExpiryDate(new DateTime(c.getExpiresAt().toEpochMilli()));
            }
            result.add(dto);
        }
        return result;
    }

    // Delete from coupon table and optionally remove from PDP also
    public void deleteCampaign(String campaignId, boolean deleteFromPdpAlso) {
        if (!StringUtils.hasText(campaignId)) {
            throw new IllegalArgumentException("campaignId is required");
        }
        Coupon coupon = couponRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("campaign not found: " + campaignId));

        // Remove coupon document first
        couponRepository.deleteById(campaignId);

        if (deleteFromPdpAlso) {
            // Clear embedded campaign from PDP for the brand
            pdpRepository.findById(coupon.getBrandId()).ifPresent(pdp -> {
                if (pdp.getCampaign() != null) {
                    pdp.setCampaign(null);
                    pdpRepository.save(pdp);
                }
            });
        }
    }

    // Helpers
    private String sanitizeCode(String code) {
        return code.trim().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    private String generateCouponCode(String brandName) {
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
}