package com.group2.glamping.model.entity;

import com.group2.glamping.model.id.IdBookingDetailOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "booking_detail_order")
public class BookingDetailOrder {

    @EmbeddedId
    private IdBookingDetailOrder idBookingDetailOrder;

    @ManyToOne
    @MapsId("bookingDetailId")
    @JoinColumn(name = "id_booking_detail", insertable = false, updatable = false)
    private BookingDetail bookingDetail;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "id_order", insertable = false, updatable = false)
    private Order order;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_amount")
    private double totalAmount;
}
