package com.group2.glamping.model.id;


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
public class IdCampSiteUtility implements Serializable {

    @Column(name = "id_camp_site")
    private int campSiteId;

    @Column(name = "id_utility")
    private int utilityId;
}