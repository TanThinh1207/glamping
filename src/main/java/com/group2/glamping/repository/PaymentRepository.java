package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findBySessionId(String sessionId);
}
