package com.group2.glamping.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "booking_detail_order")
@Builder
public class BookingDetailOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "price")
    private Double price;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "id_booking_detail")
    private BookingDetail bookingDetail;
}
