package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_booking")
    private int id_booking;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "status")
    private String status;

    @Column(name = "id_transaction")
    private String id_transaction;

    @Column(name = "completed_at")
    private LocalDateTime completedTime;
}
