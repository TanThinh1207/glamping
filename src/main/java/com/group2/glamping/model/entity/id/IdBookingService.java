package com.group2.glamping.model.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class IdBookingService implements Serializable {

    @Column(name = "id_booking")
    private int bookingId;

    @Column(name = "id_service")
    private int serviceId;
}
