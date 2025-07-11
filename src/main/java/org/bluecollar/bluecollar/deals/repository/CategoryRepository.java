package org.bluecollar.bluecollar.deals.repository;

import org.bluecollar.bluecollar.deals.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByActiveTrue();
}