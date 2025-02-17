package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.response.PaymentBaseResponse;
import com.group2.glamping.model.dto.response.PaymentResponse;
import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.model.enums.PaymentStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    void save(Payment payment);

    Double getPaidAmountByBooking(Integer orderId);

    PaymentResponse createVnPayPayment(HttpServletRequest request);

    List<PaymentBaseResponse> findAll();

    Optional<PaymentBaseResponse> findPaymentByBookingId(Integer orderId);

    List<PaymentBaseResponse> findByStatus(PaymentStatus status);

}
