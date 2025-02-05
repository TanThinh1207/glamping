package com.group2.glamping.model.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class IdBookingSelection implements Serializable {

    @Column(name = "id_booking")
    private int bookingId;

    @Column(name = "id_selection")
    private int selectionId;
}
