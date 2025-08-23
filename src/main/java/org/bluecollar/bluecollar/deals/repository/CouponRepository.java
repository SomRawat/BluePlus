package org.bluecollar.bluecollar.deals.repository;

import org.bluecollar.bluecollar.deals.model.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends MongoRepository<Coupon, String> {
    Optional<Coupon> findFirstByBrandId(String brandId);
    Optional<Coupon> findByBrandIdAndCouponCodeIgnoreCase(String brandId, String couponCode);
}