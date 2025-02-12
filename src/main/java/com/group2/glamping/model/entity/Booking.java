package com.group2.glamping.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group2.glamping.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_camp_site")
    private CampSite campSite;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "check_in_at")
    private LocalDateTime checkinTime;

    @Column(name = "check_out_at")
    private LocalDateTime checkoutTime;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "system_fee")
    private double systemFee;

    @Column(name = "net_amount")
    private double netAmount;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating")
    private int rating;

    @Column(name = "message")
    private String message;

    @OneToMany(mappedBy = "booking")
    private List<BookingSelection> bookingSelectionList;

    @OneToMany(mappedBy = "booking")
    private List<Payment> paymentList;

    @OneToMany(mappedBy = "booking")
    private List<BookingDetail> bookingDetailList;
}
