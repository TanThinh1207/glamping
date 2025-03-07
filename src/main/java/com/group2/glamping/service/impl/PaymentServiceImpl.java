package com.group2.glamping.service.impl;

import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.repository.PaymentRepository;
import com.group2.glamping.service.interfaces.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }
}
