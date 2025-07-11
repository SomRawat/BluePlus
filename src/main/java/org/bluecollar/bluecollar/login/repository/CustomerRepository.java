package org.bluecollar.bluecollar.login.repository;

import org.bluecollar.bluecollar.login.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByMobile(String mobile);
    Optional<Customer> findByGoogleId(String googleId);
    boolean existsByMobile(String mobile);
    boolean existsByGoogleId(String googleId);
}