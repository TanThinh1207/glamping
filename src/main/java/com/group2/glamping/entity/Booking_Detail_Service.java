package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "booking_detail_service")
public class Booking_Detail_Service {

    @EmbeddedId
    private int id_Booking_Detail_Service;

    @ManyToOne
    @MapsId("bookingDetailId")
    @JoinColumn(name = "id_booking_detail", insertable = false, updatable = false)
    private Booking_Detail bookingDetail;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "id_service", insertable = false, updatable = false)
    private Service Service;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private double quantity;
}
