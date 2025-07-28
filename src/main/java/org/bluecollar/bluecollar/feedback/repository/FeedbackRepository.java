package org.bluecollar.bluecollar.feedback.repository;

import org.bluecollar.bluecollar.feedback.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
}