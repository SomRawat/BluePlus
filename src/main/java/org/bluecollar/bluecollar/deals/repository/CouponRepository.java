package org.bluecollar.bluecollar.deals.repository;

import org.bluecollar.bluecollar.deals.model.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends MongoRepository<Coupon, String> {
    List<Coupon> findByCustomerId(String customerId);
    Optional<Coupon> findByCustomerIdAndBrandIdAndRedeemedFalse(String customerId, String brandId);
    List<Coupon> findByCustomerIdAndRedeemedFalse(String customerId);
    Optional<Coupon> findByCouponCode(String couponCode);
}