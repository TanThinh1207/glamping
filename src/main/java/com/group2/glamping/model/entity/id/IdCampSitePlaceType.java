package com.group2.glamping.model.entity.id;

import jakarta.persistence.Embeddable;

@Embeddable
public class IdCampSitePlaceType {
    private int campSiteId;
    private int placeTypeId;
}
