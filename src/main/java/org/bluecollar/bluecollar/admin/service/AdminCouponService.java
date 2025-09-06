package org.bluecollar.bluecollar.admin.service;

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
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Service
public class AdminCouponService {

    private final PDPRepository pdpRepository;
    private final CouponRepository couponRepository;

    public AdminCouponService(PDPRepository pdpRepository, CouponRepository couponRepository) {
        this.pdpRepository = pdpRepository;
        this.couponRepository = couponRepository;
    }

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
        if (request.getNoOfCoupons() <= 0) {
            throw new IllegalArgumentException("totalLimit must be > 0");
        }
        if (request.getExpiryDays() == null || request.getExpiryDays() <= 0) {
            throw new IllegalArgumentException("expiryDays must be provided and > 0");
        }

        // Load PDP
        PDP pdp = pdpRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("PDP page not found for id: " + request.getBrandId()));

        // Prepare or update embedded campaign in PDP
        PDP.CouponCampaign campaign = pdp.getCampaign();
        if (isUpdate) {
            if (campaign == null) {
                campaign = new PDP.CouponCampaign();
                campaign.setId(UUID.randomUUID().toString());
                campaign.setCreatedAt(new Date());
            }
        } else {
            if (campaign != null) {
                isUpdate = true;
            } else {
                campaign = new PDP.CouponCampaign();
                campaign.setId(UUID.randomUUID().toString());
                campaign.setCreatedAt(new Date());
            }
        }

        campaign.setCampaignName(request.getCampaignName().trim());
        campaign.setCity(StringUtils.hasText(request.getCity()) ? request.getCity().trim() : null);

        // Handle coupon code: use provided or generate
        String code = StringUtils.hasText(request.getCouponCode())
                ? sanitizeCode(request.getCouponCode())
                : generateCouponCode(pdp.getBrandName());
        
        campaign.setCouponCode(code);
        campaign.setNoOfCoupons(request.getNoOfCoupons());
        campaign.setActive(!Boolean.FALSE.equals(request.getActive()));
        campaign.setExpiryDays(request.getExpiryDays());

        Instant expiryInstant = LocalDate.now(ZoneOffset.UTC)
                .plusDays(request.getExpiryDays().longValue())
                .atTime(23, 59, 59)
                .toInstant(ZoneOffset.UTC);
        campaign.setExpiresAt(Date.from(expiryInstant));

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
                coupon = couponRepository.findFirstById(request.getBrandId())
                        .orElseGet(() -> {
                            Coupon c = new Coupon();
                            c.setId(UUID.randomUUID().toString());
                            c.setCreatedAt(new Date());
                            return c;
                        });
            }
        } else {
            coupon = new Coupon();
            coupon.setId(UUID.randomUUID().toString());
            coupon.setCreatedAt(new Date());
        }
        
        coupon.setBrandId(request.getBrandId());
        coupon.setCampaignName(request.getCampaignName().trim());
        coupon.setCity(StringUtils.hasText(request.getCity()) ? request.getCity().trim() : null);
        coupon.setCouponCode(code);
        coupon.setCouponImageUrl(request.getCouponImageUrl());
        coupon.setNoOfCoupons(request.getNoOfCoupons());
        coupon.setActive(Boolean.TRUE.equals(request.getActive()) || request.getActive() == null);
        coupon.setExpiresAt(Date.from(expiryInstant));

        couponRepository.save(coupon);
        return campaign.getId();
    }

    public List<CouponRequest> getAllCampaigns() {
        List<Coupon> coupons = couponRepository.findAll();
        if (coupons.isEmpty()) {
            return Collections.emptyList();
        }
        List<CouponRequest> result = new ArrayList<>(coupons.size());
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        for (Coupon c : coupons) {
            CouponRequest dto = new CouponRequest();
            dto.setId(c.getId());
            dto.setActive(c.getActive());
            dto.setCampaignName(c.getCampaignName());
            dto.setBrandId(c.getBrandId());
            dto.setCity(c.getCity());
            dto.setCouponCode(c.getCouponCode());
            dto.setCouponImageUrl(c.getCouponImageUrl());
            dto.setNoOfCoupons(c.getNoOfCoupons());
            dto.setExpiryDate(c.getExpiresAt() != null ? df.format(c.getExpiresAt()) : null);
            result.add(dto);
        }
        return result;
    }

    public void deleteCampaign(String campaignId, boolean deleteFromPdpAlso) {
        if (!StringUtils.hasText(campaignId)) {
            throw new IllegalArgumentException("campaignId is required");
        }
        Coupon coupon = couponRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("campaign not found: " + campaignId));

        couponRepository.deleteById(campaignId);

        if (deleteFromPdpAlso) {
            pdpRepository.findById(coupon.getBrandId()).ifPresent(pdp -> {
                if (pdp.getCampaign() != null) {
                    pdp.setCampaign(null);
                    pdpRepository.save(pdp);
                }
            });
        }
    }

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

    public CouponRequest getCouponByBrandId(String brandId) {
        Coupon coupon = couponRepository.findByBrandId(brandId)
                .orElseThrow(() -> new IllegalArgumentException("No coupon found for brandId: " + brandId));
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        CouponRequest dto = new CouponRequest();
        dto.setId(coupon.getId());
        dto.setActive(coupon.getActive());
        dto.setCampaignName(coupon.getCampaignName());
        dto.setBrandId(coupon.getBrandId());
        dto.setCity(coupon.getCity());
        dto.setCouponCode(coupon.getCouponCode());
        dto.setCouponImageUrl(coupon.getCouponImageUrl());
        dto.setNoOfCoupons(coupon.getNoOfCoupons());
        dto.setExpiryDate(coupon.getExpiresAt() != null ? df.format(coupon.getExpiresAt()) : null);
        return dto;
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