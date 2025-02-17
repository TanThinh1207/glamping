package com.group2.glamping.model.dto.response;

import com.group2.glamping.model.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PaymentBaseResponse {

    int id;
    int id_booking;
    String paymentMethod;
    double totalAmount;
    PaymentStatus status;
    String id_transaction;
    LocalDateTime created_at;

}
