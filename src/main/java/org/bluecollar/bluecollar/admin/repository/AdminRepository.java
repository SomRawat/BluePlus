package org.bluecollar.bluecollar.admin.repository;

import org.bluecollar.bluecollar.admin.model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
    Optional<Admin> findByEmailAndActive(String email, boolean active);
    List<Admin> findByActive(boolean active);
}