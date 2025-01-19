package com.group2.glamping.entity;


import com.group2.glamping.entity.id.IdCampFacility;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "camp_facility")
public class CampFacility {

    @EmbeddedId
    private IdCampFacility idCampFacility;

    @ManyToOne
    @MapsId("campId")
    @JoinColumn(name = "id_camp", insertable = false, updatable = false)
    private Camp camp;

    @ManyToOne
    @MapsId("facilityId")
    @JoinColumn(name = "id_facility", insertable = false, updatable = false)
    private Facility facility;

    @Column(name = "status")
    private boolean status;
}
