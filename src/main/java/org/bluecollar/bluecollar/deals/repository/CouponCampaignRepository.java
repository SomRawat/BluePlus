package org.bluecollar.bluecollar.deals.repository;

import org.bluecollar.bluecollar.deals.model.CouponCampaign;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponCampaignRepository extends MongoRepository<CouponCampaign, String> {
    List<CouponCampaign> findByBrandIdAndIsActiveTrue(String brandId);
    Optional<CouponCampaign> findByIdAndIsActiveTrue(String id);
}