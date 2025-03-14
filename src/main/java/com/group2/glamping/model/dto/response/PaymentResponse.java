package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class PaymentResponse {

    public int id;
    public String paymentMethod;
    public Double totalAmount;
    public PaymentStatus status;
    public String transactionId;
    public String sessionId;
    public String paymentUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime completedAt;

    public static PaymentResponse fromEntity(Payment payment) {
        if (payment == null) {
            System.out.println("payment is null");
            return null;
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentMethod(payment.getPaymentMethod())
                .totalAmount(payment.getTotalAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .sessionId(payment.getSessionId())
                .paymentUrl(payment.getUrl())
                .completedAt(payment.getCompletedTime())
                .build();
    }

}