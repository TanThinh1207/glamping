package com.group2.glamping.model.entity;

import com.group2.glamping.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "payment")
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_booking")
    private Booking booking;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "id_transaction")
    private String transactionId;

    @Column(name = "completed_at")
    private LocalDateTime completedTime;
}
