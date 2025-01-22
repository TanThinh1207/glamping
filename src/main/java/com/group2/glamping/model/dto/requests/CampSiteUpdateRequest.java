package com.group2.glamping.model.dto.requests;

import lombok.Data;

@Data
public class CampSiteUpdateRequest {

    String name;
    String address;
    double latitude;
    double longitude;


}
