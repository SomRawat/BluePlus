package org.bluecollar.bluecollar.login.repository;

import org.bluecollar.bluecollar.login.model.OtpSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpSessionRepository extends MongoRepository<OtpSession, String> {
    Optional<OtpSession> findByMobileAndVerifiedFalse(String mobile);
    void deleteByMobile(String mobile);
}