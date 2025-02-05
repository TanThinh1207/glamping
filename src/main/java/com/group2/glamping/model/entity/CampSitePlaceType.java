package com.group2.glamping.model.entity;

import com.group2.glamping.model.entity.id.IdCampSitePlaceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "camp_site_place_type")
public class CampSitePlaceType {

    @EmbeddedId
    private IdCampSitePlaceType campSitePlaceTypeId;

    @ManyToOne
    @MapsId("campSiteId")
    @JoinColumn(name = "id_camp_site", nullable = false, updatable = false)
    private CampSite campSite;

    @ManyToOne
    @MapsId("placeTypeId")
    @JoinColumn(name = "id_place_type", nullable = false, updatable = false)
    private PlaceType placeType;
}
