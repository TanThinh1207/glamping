package com.group2.glamping.model.dto.requests;

import lombok.Data;

import java.util.List;

@Data
public class CampSiteRequest {

    int hostId;
    String name;
    String address;
    double latitude;
    double longitude;
    String city;
    List<String> imagePathList;
    List<PlaceTypeRequest> campSitePlaceTypes; // admin
    List<SelectionRequest> campSiteSelections; // host
    List<UtilityRequest> campSiteUtilities; //admin
    List<CampTypeUpdateRequest> campTypeList; // host

}
