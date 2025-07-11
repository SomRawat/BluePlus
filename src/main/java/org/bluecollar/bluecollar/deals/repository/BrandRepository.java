package org.bluecollar.bluecollar.deals.repository;

import org.bluecollar.bluecollar.deals.model.Brand;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends MongoRepository<Brand, String> {
    List<Brand> findByActiveTrue();
    Optional<Brand> findByIdAndActiveTrue(String id);
}