package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private int userId;

    @Column(name = "id_camp_site")
    private int campSiteId;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount")
    private double totalAmount;

    @OneToMany(mappedBy = "booking",cascade = CascadeType.ALL)
    private List<BookingDetailService> bookingDetailServiceList = new ArrayList<>();

}
