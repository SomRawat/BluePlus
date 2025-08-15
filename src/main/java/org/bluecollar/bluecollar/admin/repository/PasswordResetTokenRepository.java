package org.bluecollar.bluecollar.admin.repository;

import org.bluecollar.bluecollar.admin.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByEmailAndOtpAndUsedFalseAndExpiryTimeAfter(String email, String otp, LocalDateTime currentTime);
    Optional<PasswordResetToken> findByEmailAndUsedFalseAndCreatedAtAfter(String email, LocalDateTime createdAfter);
    void deleteByEmail(String email);
}