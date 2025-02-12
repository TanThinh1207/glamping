package com.group2.glamping.model.entity.id;


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
public class IdCampTypeFacility implements Serializable {

    private int campTypeId;

    private int facilityId;
}