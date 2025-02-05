package com.group2.glamping.model.entity;


import com.group2.glamping.model.entity.id.IdCampSiteSelection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "camp_site_selection")
public class CampSiteSelection {

    @EmbeddedId
    private IdCampSiteSelection campSiteSelectionId;


    @ManyToOne
    @MapsId("campSiteId")
    @JoinColumn(name = "id_camp_site")
    private CampSite campSite;

    @ManyToOne
    @MapsId("selectionId")
    @JoinColumn(name = "id_selection")
    private Selection selection;
}
