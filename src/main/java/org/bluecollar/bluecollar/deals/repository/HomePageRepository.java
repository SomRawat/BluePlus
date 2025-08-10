package org.bluecollar.bluecollar.deals.repository;

import org.bluecollar.bluecollar.deals.model.HomePage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomePageRepository extends MongoRepository<HomePage, String> {
    HomePage findByActiveTrue();
}