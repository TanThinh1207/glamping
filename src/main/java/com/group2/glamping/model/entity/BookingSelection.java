package com.group2.glamping.model.entity;

import com.group2.glamping.model.entity.id.IdBookingSelection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "booking_selection")
@Builder
public class BookingSelection {

    @EmbeddedId
    private IdBookingSelection idBookingService;

    @ManyToOne
    @MapsId("bookingId")
    @JoinColumn(name = "id_booking", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne
    @MapsId("selectionId")
    @JoinColumn(name = "id_selection", insertable = false, updatable = false)
    private Selection selection;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private Integer quantity;
}
