package com.group2.glamping.model.entity.id;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class IdCampTypeFacility implements Serializable {

    private int campTypeId;
    private int facilityId;
}
