package com.group2.glamping.service.impl;

import com.group2.glamping.configuration.VNPAYConfig;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.response.PaymentBaseResponse;
import com.group2.glamping.model.dto.response.PaymentResponse;
import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.model.enums.PaymentStatus;
import com.group2.glamping.repository.PaymentRepository;
import com.group2.glamping.service.interfaces.PaymentService;
import com.group2.glamping.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final VNPAYConfig vnpayConfig;

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public Double getPaidAmountByBooking(Integer orderId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(orderId);
        double amount = 0.0;
        if (paymentOptional.isPresent()) {
            amount = paymentOptional.get().getTotalAmount();
        } else {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }

        return amount;
    }

    @Override
    public PaymentResponse createVnPayPayment(HttpServletRequest request) {
        double d = Double.parseDouble(request.getParameter("amount")) * 25455.50;
        int i = (int) Math.ceil(d);
        long l = i * 100L;
        String s = String.valueOf(l);
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnpayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", s);
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    @Override
    public List<PaymentBaseResponse> findAll() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(payment -> PaymentBaseResponse.builder()
                        .paymentMethod(payment.getPaymentMethod())
                        .created_at(payment.getCompletedTime())
                        .id(payment.getId())
                        .id_booking(payment.getBooking().getId())
                        .id_transaction(payment.getTransactionId())
                        .totalAmount(payment.getTotalAmount())
                        .status(payment.getStatus())
                        .build())
                .toList();
    }

    @Override
    public Optional<PaymentBaseResponse> findPaymentByBookingId(Integer orderId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(orderId);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            PaymentBaseResponse paymentBaseResponse = PaymentBaseResponse.builder()
                    .id(payment.getId())
                    .id_booking(payment.getBooking().getId()) // Assuming Payment has a Booking entity
                    .paymentMethod(payment.getPaymentMethod())
                    .totalAmount(payment.getTotalAmount())
                    .status(payment.getStatus())
                    .id_transaction(payment.getTransactionId())
                    .created_at(payment.getCompletedTime())
                    .build();
            return Optional.of(paymentBaseResponse);
        } else {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
    }

    @Override
    public List<PaymentBaseResponse> findByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findAllByStatus(status);
        return payments.stream()
                .map(payment -> PaymentBaseResponse.builder()
                        .paymentMethod(payment.getPaymentMethod())
                        .created_at(payment.getCompletedTime())
                        .id(payment.getId())
                        .id_booking(payment.getBooking().getId())
                        .id_transaction(payment.getTransactionId())
                        .totalAmount(payment.getTotalAmount())
                        .status(payment.getStatus())
                        .build())
                .toList();
    }
}
