package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.deals.model.PDP;
import org.bluecollar.bluecollar.deals.model.UserCoupon;
import org.bluecollar.bluecollar.deals.repository.PDPRepository;
import org.bluecollar.bluecollar.deals.repository.UserCouponRepository;
import org.bluecollar.bluecollar.login.model.Customer;
import org.bluecollar.bluecollar.login.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomerCouponService {

    @Autowired
    private UserCouponRepository userCouponRepository;
    
    @Autowired
    private PDPRepository pdpRepository;
    
    @Autowired
    private CustomerRepository customerRepository;



    public String redeemCoupon(String customerId, String brandId) {
        // Check if user already has a coupon for this brand
        if (userCouponRepository.existsByCustomerIdAndBrandId(customerId, brandId)) {
            throw new RuntimeException("You already have a coupon for this brand");
        }

        // Get PDP and campaign details
        PDP pdp = pdpRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        
        PDP.CouponCampaign campaign = pdp.getCampaign();
        if (campaign == null || !campaign.isActive()) {
            throw new RuntimeException("No active campaign for this brand");
        }

        // Check if campaign has expired
        if (campaign.getExpiresAt() != null && campaign.getExpiresAt().before(new Date())) {
            throw new RuntimeException("Campaign has expired");
        }

        // Check if campaign has available coupons
        long usedCoupons = userCouponRepository.countByCampaignId(campaign.getId());
        if (usedCoupons >= campaign.getNoOfCoupons()) {
            throw new RuntimeException("All coupons for this campaign have been used");
        }

        // Create user coupon with campaign's coupon code
        UserCoupon userCoupon = new UserCoupon(
            customerId, 
            brandId, 
            campaign.getId(), 
            campaign.getCouponCode(), 
            campaign.getExpiresAt()
        );
        userCoupon.setRedeemed(true);
        userCoupon.setRedeemedAt(new Date());
        
        userCouponRepository.save(userCoupon);

        // Add to customer's redeemed coupons list
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null && !customer.getRedeemedCoupons().contains(brandId)) {
            customer.getRedeemedCoupons().add(brandId);
            customerRepository.save(customer);
        }

        return campaign.getCouponCode();
    }

    public UserCoupon getUserCoupon(String customerId, String brandId) {
        return userCouponRepository.findByCustomerIdAndBrandId(customerId, brandId).orElse(null);
    }


}