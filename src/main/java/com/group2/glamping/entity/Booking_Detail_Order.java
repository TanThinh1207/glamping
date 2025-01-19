package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "order")
public class Booking_Detail_Order {

    @EmbeddedId
    private int id_Booking_Detail_Order;

    @ManyToOne
    @MapsId("bookingDetailId")
    @JoinColumn(name = "id_booking_detail", insertable = false, updatable = false)
    private Booking_Detail bookingDetail;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "id_order", insertable = false, updatable = false)
    private Order order;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_amount")
    private double totalAmount;
}
