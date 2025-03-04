package com.group2.glamping.service.impl;

import com.group2.glamping.configuration.VNPAYConfig;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.response.PaymentBaseResponse;
import com.group2.glamping.model.dto.response.PaymentResponse;
import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.model.enums.PaymentStatus;
import com.group2.glamping.repository.BookingRepository;
import com.group2.glamping.repository.PaymentRepository;
import com.group2.glamping.service.interfaces.PaymentService;
import com.group2.glamping.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingService;
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
        try {
            String amountParam = request.getParameter("amount");
            if (amountParam == null || amountParam.isEmpty()) {
                throw new IllegalArgumentException("Amount is required");
            }

            BigDecimal amount = new BigDecimal(amountParam).setScale(2, BigDecimal.ROUND_CEILING);

            String bankCode = request.getParameter("bankCode");
            String bookingId = request.getParameter("bookingId");
            if (bookingId == null || bookingId.isEmpty()) {
                throw new IllegalArgumentException("Booking ID is required");
            }


            Map<String, String> vnpParamsMap = vnpayConfig.getVNPayConfig();
            vnpParamsMap.put("vnp_Amount", amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
            vnpParamsMap.put("vnp_TxnRef", bookingId);

            if (bankCode != null && !bankCode.isEmpty()) {
                vnpParamsMap.put("vnp_BankCode", bankCode);
            }
            vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

            String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
            String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
            String vnpSecureHash = VNPayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

            String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;
            paymentRepository.save(Payment.builder()
                    .booking(bookingService.getReferenceById(Integer.parseInt(bookingId)))
                    .totalAmount(Double.parseDouble(amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString()))
                    .paymentMethod("VNPay")
                    .status(PaymentStatus.Pending)
                    .build());
            return PaymentResponse.builder()
                    .code("ok")
                    .message("success")
                    .paymentUrl(paymentUrl)
                    .build();
        } catch (Exception e) {
            return PaymentResponse.builder()
                    .code("error")
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentResponse refundPayment(Integer paymentId, BigDecimal refundAmount, String reason) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

            if (!PaymentStatus.Completed.equals(payment.getStatus())) {
                throw new IllegalStateException("Only successful payments can be refunded");
            }

            Map<String, String> vnpParamsMap = vnpayConfig.getVNPayConfig();
            vnpParamsMap.put("vnp_Command", "refund");
            vnpParamsMap.put("vnp_TxnRef", String.valueOf(payment.getId()));
            vnpParamsMap.put("vnp_Amount", refundAmount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
            vnpParamsMap.put("vnp_TransactionNo", payment.getTransactionId());
            vnpParamsMap.put("vnp_OrderInfo", reason);

            String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
            String vnpSecureHash = VNPayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
            vnpParamsMap.put("vnp_SecureHash", vnpSecureHash);

            String refundResponse = VNPayUtil.sendPostRequest(vnpayConfig.getVnp_PayUrl(), vnpParamsMap);

            if (refundResponse.contains("00")) {
                paymentRepository.save(payment);
                return PaymentResponse.builder()
                        .code("ok")
                        .message("Refund successful")
                        .build();
            } else {
                return PaymentResponse.builder()
                        .code("error")
                        .message("Refund failed")
                        .build();
            }
        } catch (Exception e) {
            return PaymentResponse.builder()
                    .code("error")
                    .message(e.getMessage())
                    .build();
        }
    }


    @Override
    public void updatePaymentStatus(Integer paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        payment.setStatus(status);
        paymentRepository.save(payment);
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
