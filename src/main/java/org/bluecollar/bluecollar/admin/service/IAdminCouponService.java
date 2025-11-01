package org.bluecollar.bluecollar.admin.service;

import org.bluecollar.bluecollar.deals.dto.CouponRequest;

import java.util.List;

public interface IAdminCouponService {
    
    String upsertCouponCampaign(CouponRequest request);
    
    List<CouponRequest> getAllCampaigns();
    
    void deleteCampaign(String campaignId, boolean deleteFromPdpAlso);
    
    CouponRequest getCouponByBrandId(String brandId);
}

