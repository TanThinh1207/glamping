package com.group2.glamping.model.entity.id;


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
public class IdCampFacility implements Serializable {

    @Column(name = "id_camp")
    private int campId;

    @Column(name = "id_facility")
    private int facilityId;
}
