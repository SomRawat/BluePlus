package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.common.exception.BadRequestException;
import org.bluecollar.bluecollar.common.exception.ResourceNotFoundException;
import org.bluecollar.bluecollar.deals.model.PDP;
import org.bluecollar.bluecollar.deals.model.UserCoupon;
import org.bluecollar.bluecollar.deals.repository.PDPRepository;
import org.bluecollar.bluecollar.deals.repository.UserCouponRepository;
import org.bluecollar.bluecollar.login.model.Customer;
import org.bluecollar.bluecollar.login.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CustomerCouponService {

    private final UserCouponRepository userCouponRepository;
    private final PDPRepository pdpRepository;
    private final CustomerRepository customerRepository;

    public CustomerCouponService(UserCouponRepository userCouponRepository,
                                PDPRepository pdpRepository,
                                CustomerRepository customerRepository) {
        this.userCouponRepository = userCouponRepository;
        this.pdpRepository = pdpRepository;
        this.customerRepository = customerRepository;
    }



    @Transactional
    public synchronized String redeemCoupon(String customerId, String brandId) {
        // Validate input
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new BadRequestException("Customer ID is required");
        }
        if (brandId == null || brandId.trim().isEmpty()) {
            throw new BadRequestException("Brand ID is required");
        }
        
        // Check if user already has a coupon for this brand
        if (userCouponRepository.existsByCustomerIdAndBrandId(customerId, brandId)) {
            throw new BadRequestException("You have already redeemed a coupon for this brand");
        }

        // Get PDP and campaign details
        PDP pdp = pdpRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + brandId));
        
        PDP.CouponCampaign campaign = pdp.getCampaign();
        if (campaign == null) {
            throw new BadRequestException("No coupon campaign available for this brand");
        }
        
        if (!campaign.isActive()) {
            throw new BadRequestException("This coupon campaign is no longer active");
        }

        // Check if campaign has expired
        Date now = new Date();
        if (campaign.getExpiresAt() != null && campaign.getExpiresAt().before(now)) {
            throw new BadRequestException("This coupon campaign has expired");
        }

        // Check if campaign has available coupons (with synchronized check to prevent race condition)
        long usedCoupons = userCouponRepository.countByCampaignId(campaign.getId());
        if (usedCoupons >= campaign.getNoOfCoupons()) {
            throw new BadRequestException("All coupons for this campaign have been claimed");
        }

        // Verify customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        // Create user coupon with campaign's coupon code
        UserCoupon userCoupon = new UserCoupon(
            customerId, 
            brandId, 
            campaign.getId(), 
            campaign.getCouponCode(), 
            campaign.getExpiresAt()
        );
        userCoupon.setRedeemed(true);
        userCoupon.setRedeemedAt(now);
        
        userCouponRepository.save(userCoupon);

        // Add to customer's redeemed coupons list
        if (!customer.getRedeemedCoupons().contains(brandId)) {
            customer.getRedeemedCoupons().add(brandId);
            customerRepository.save(customer);
        }

        return campaign.getCouponCode();
    }

    public UserCoupon getUserCoupon(String customerId, String brandId) {
        return userCouponRepository.findByCustomerIdAndBrandId(customerId, brandId).orElse(null);
    }


}