package com.group2.glamping.model.entity.id;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class IdBookingService implements Serializable {

    private int bookingId;
    private int serviceId;
}
