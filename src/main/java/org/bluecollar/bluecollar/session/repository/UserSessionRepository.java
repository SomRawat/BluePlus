package org.bluecollar.bluecollar.session.repository;

import org.bluecollar.bluecollar.session.model.UserSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends MongoRepository<UserSession, String> {
    Optional<UserSession> findBySessionToken(String sessionToken);
}