package com.group2.glamping.entity.id;


import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class IdCampFacility implements Serializable {

    private int campId;
    private int facilityId;
}
