package com.group2.glamping.model.entity;

import com.group2.glamping.model.entity.id.IdCampSiteUtility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "camp_site_utility")
public class CampSiteUtility {

    @EmbeddedId
    private IdCampSiteUtility idCampSiteUtility;

    @ManyToOne
    @MapsId("campSiteId")
    @JoinColumn(name = "id_camp_site", insertable = false, updatable = false)
    private CampSite campSite;

    @ManyToOne
    @MapsId("utilityId")
    @JoinColumn(name = "id_utility", insertable = false, updatable = false)
    private Utility utility;

    @Column(name = "status")
    private boolean status;

}
