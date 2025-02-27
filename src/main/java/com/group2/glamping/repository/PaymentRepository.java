package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findAllByStatus(PaymentStatus status);
}
