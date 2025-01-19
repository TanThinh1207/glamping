package com.group2.glamping.entity;

import com.group2.glamping.entity.id.IdBookingDetailService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "booking_detail_service")
public class BookingDetailService {

    @EmbeddedId
    private IdBookingDetailService idBookingDetailService;

    @ManyToOne
    @MapsId("bookingDetailId")
    @JoinColumn(name = "id_booking_detail", insertable = false, updatable = false)
    private BookingDetail bookingDetail;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "id_service", insertable = false, updatable = false)
    private Service service;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private double quantity;
}
