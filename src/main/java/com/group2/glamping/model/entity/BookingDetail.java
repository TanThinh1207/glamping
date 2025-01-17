package com.group2.glamping.model.entity;

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
public class BookingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_booking")
    private int bookingId;

    @Column(name = "id_camp")
    private int campId;

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

    @OneToMany(mappedBy = "bookingDetail")
    private List<BookingDetailOrder> bookingDetailOrders;

    @OneToMany(mappedBy = "bookingDetail",cascade = CascadeType.ALL)
    private List<BookingDetailService> bookingDetailServiceList;
}
