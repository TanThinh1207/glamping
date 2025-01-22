package com.group2.glamping.model.entity;

import com.group2.glamping.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_camp_site")
    private CampSite campSite;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "total_amount")
    private double totalAmount;

    @OneToMany(mappedBy = "booking")
    private List<BookingService> bookingServiceList;

    @OneToMany(mappedBy = "booking")
    private List<Payment> paymentList;

    @OneToMany(mappedBy = "booking")
    private List<BookingDetail> bookingDetailList;
}
