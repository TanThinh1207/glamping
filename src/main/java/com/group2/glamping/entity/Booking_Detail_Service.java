package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "booking_detail_service")
public class Booking_Detail_Service {

    @ManyToOne
    @JoinColumn(name = "id_booking_detail")
    private Booking_Detail bookingDetail;

    @ManyToOne
    @JoinColumn(name = "id_service")
    private  Service service;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private double quantity;
}
