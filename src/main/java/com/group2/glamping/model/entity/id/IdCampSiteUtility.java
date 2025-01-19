package com.group2.glamping.model.entity.id;


import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class IdCampSiteUtility implements Serializable {

    private int campSiteId;
    private int utilityId;
}