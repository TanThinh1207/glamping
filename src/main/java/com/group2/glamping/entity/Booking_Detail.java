package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "booking_detail")
public class Booking_Detail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_booking")
    private int id_booking;

    @Column(name = "id_camp")
    private int id_camp;

    @Column(name = "check_in_at")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_at")
    private LocalDateTime checkOutTime;

    @Column(name = "amount")
    private double amount;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating")
    private int rating;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "add_on")
    private double addOn;

    @OneToMany(mappedBy = "booking_detail")
    private List<Booking_Detail_Order> booking_Detail_Orders;
}
