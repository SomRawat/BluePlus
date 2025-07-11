package org.bluecollar.bluecollar.payment.repository;

import org.bluecollar.bluecollar.payment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    List<Payment> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    List<Payment> findByStatus(String status);
    List<Payment> findByStatusAndCreatedAtAfter(String status, java.time.LocalDateTime dateTime);
}