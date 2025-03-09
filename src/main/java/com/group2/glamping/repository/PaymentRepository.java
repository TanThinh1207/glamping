package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findBySessionId(String sessionId);

    @Query("SELECT p FROM payment p WHERE p.completedTime < :timeLimit AND p.status <> :status")
    List<Payment> findPaymentsOlderThan24Hours(@Param("timeLimit") LocalDateTime timeLimit, @Param("status") PaymentStatus status);
}
