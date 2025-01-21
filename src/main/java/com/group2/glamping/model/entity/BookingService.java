package com.group2.glamping.model.entity;

import com.group2.glamping.model.entity.id.IdBookingService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "booking_service")
public class BookingService {

    @EmbeddedId
    private IdBookingService idBookingService;

    @ManyToOne
    @MapsId("bookingId")
    @JoinColumn(name = "id_booking", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "id_service", insertable = false, updatable = false)
    private Service service;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private double quantity;
}
