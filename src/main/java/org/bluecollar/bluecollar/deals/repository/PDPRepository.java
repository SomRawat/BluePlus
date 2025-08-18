package org.bluecollar.bluecollar.deals.repository;

import org.bluecollar.bluecollar.deals.model.PDP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PDPRepository extends MongoRepository<PDP, String> {
    // id is now @Id, use findById() for queries
}