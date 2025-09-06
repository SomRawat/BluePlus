package org.bluecollar.bluecollar.deals.repository;

import org.bluecollar.bluecollar.deals.model.UserCoupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends MongoRepository<UserCoupon, String> {
    Optional<UserCoupon> findByCustomerIdAndBrandId(String customerId, String brandId);
    List<UserCoupon> findByCustomerId(String customerId);
    boolean existsByCustomerIdAndBrandId(String customerId, String brandId);
    long countByCampaignId(String campaignId);
}