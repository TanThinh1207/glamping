package com.group2.glamping.model.entity.id;

import jakarta.persistence.Embeddable;

@Embeddable
public class IdBookingService {

    private int bookingId;
    private int serviceId;
}
