package com.group2.glamping.model.entity.id;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class IdCampTypeFacility implements Serializable {

    private int campTypeId;
    private int facilityId;
}
