package com.group2.glamping.model.entity;

import com.group2.glamping.model.entity.id.IdCampTypeFacility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "camp_type_facility")
public class CampTypeFacility {

    @EmbeddedId
    private IdCampTypeFacility idCampTypeFacility;

    @ManyToOne
    @MapsId("campTypeId")
    @JoinColumn(name = "id_camp_type", insertable = false, updatable = false)
    private CampType campType;

    @ManyToOne
    @MapsId("facilityId")
    @JoinColumn(name = "id_facility", insertable = false, updatable = false)
    private Facility facility;

    @Column(name = "status")
    private boolean status;
}
