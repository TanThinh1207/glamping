package com.group2.glamping.model.entity;

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

    @ManyToOne(fetch = FetchType.EAGER)
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

    @Column(name = "check_in_at")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_at")
    private LocalDateTime checkOutTime;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "system_fee", columnDefinition = "DOUBLE DEFAULT 0.0", nullable = false)
    private Double systemFee = 0.0;

    @Column(name = "net_amount", columnDefinition = "DOUBLE DEFAULT 0.0", nullable = false)
    private Double netAmount = 0.0;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "message")
    private String message;

    @OneToMany(mappedBy = "booking")
    private List<BookingSelection> bookingSelectionList;

    @OneToMany(mappedBy = "booking")
    private List<Payment> paymentList;

    @OneToMany(mappedBy = "booking")
    private List<BookingDetail> bookingDetailList;
}
