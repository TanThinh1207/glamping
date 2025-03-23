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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_camp_site")
    @JsonIgnore
    private CampSite campSite;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "check_in_at", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_at", nullable = false)
    private LocalDateTime checkOutTime;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Builder.Default
    @Column(name = "system_fee", columnDefinition = "DOUBLE DEFAULT 0.0", nullable = false)
    private Double systemFee = 0.0;

    @Builder.Default
    @Column(name = "net_amount", columnDefinition = "DOUBLE DEFAULT 0.0", nullable = false)
    private Double netAmount = 0.0;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "message")
    private String message;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSelection> bookingSelectionList;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> paymentList;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingDetail> bookingDetailList;

    @Override
    public String toString() {
        return "Booking(id=" + this.id + ")";
    }
}
