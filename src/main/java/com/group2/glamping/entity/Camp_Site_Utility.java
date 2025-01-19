package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "camp_site_utility")
public class Camp_Site_Utility {

    @EmbeddedId
    private int id_Camp_Site_Utility;

    @ManyToOne
    @MapsId("campSiteId")
    @JoinColumn(name = "id_camp_site", insertable = false, updatable = false)
    private Camp_Site campSite;

    @ManyToOne
    @MapsId("utilityId")
    @JoinColumn(name = "id_utility", insertable = false, updatable = false)
    private Utility utility;

    @Column(name = "status")
    private boolean status;

}
