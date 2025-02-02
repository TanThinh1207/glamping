package com.group2.glamping.model.entity;

import com.group2.glamping.model.enums.BookingDetailStatus;
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

    @ManyToOne
    @JoinColumn(name = "id_booking")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "id_camp_type")
    private CampType campType;

    @ManyToOne
    @JoinColumn(name = "id_camp")
    private Camp camp;

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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingDetailStatus status;

    @OneToMany(mappedBy = "bookingDetail")
    private List<BookingDetailOrder> bookingDetailOrders;
}
