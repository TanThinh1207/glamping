package com.group2.glamping.model.entity.id;


import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class IdBookingDetailService implements Serializable {

    private int bookingDetailId;
    private int serviceId;
}