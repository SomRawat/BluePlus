package org.bluecollar.bluecollar.deals.repository;

import org.bluecollar.bluecollar.deals.model.PLP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PLPRepository extends MongoRepository<PLP, String> {
    PLP findByCategoryIdAndActiveTrue(String categoryId);
    // categoryId is now @Id, use findById() for queries
}