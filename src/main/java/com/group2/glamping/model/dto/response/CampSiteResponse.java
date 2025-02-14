package com.group2.glamping.model.dto.response;

import com.group2.glamping.model.enums.CampSiteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampSiteResponse {

    private int id;
    private String name;
    private String address;
    private String city;
    private double latitude;
    private double longitude;
    private LocalDateTime createdTime;
    private CampSiteStatus status;
    private String message;
    private Double depositRate;
    private List<ImageResponse> imageList;
    private List<SelectionResponse> campSiteSelectionsList;
    private List<PlaceTypeResponse> campSitePlaceTypeList;
    private List<UtilityResponse> campSiteUtilityList;
    private List<CampTypeResponse> campSiteCampTypeList;
}
