package com.group2.glamping.model.id;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdBookingDetailOrder implements Serializable {

    @Column(name = "id_booking_detail")
    private int bookingDetailId;

    @Column(name = "id_order")
    private int orderId;
}