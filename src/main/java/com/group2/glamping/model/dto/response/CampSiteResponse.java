package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.dto.response.filter.CampTypeResponseFilter;
import com.group2.glamping.model.dto.response.filter.PlaceTypeResponseFilter;
import com.group2.glamping.model.dto.response.filter.SelectionResponseFilter;
import com.group2.glamping.model.dto.response.filter.UtilityResponseFilter;
import com.group2.glamping.model.enums.CampSiteStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonFilter("dynamicFilter")
public class CampSiteResponse {

    private int id;
    private String name;
    private String address;
    private String city;
    private double latitude;
    private double longitude;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private CampSiteStatus status;
    private String message;
    private String description;
    private Double depositRate;
    private UserResponse user;
    private List<ImageResponse> imageList;
    private List<SelectionResponseFilter> campSiteSelectionsList;
    private List<PlaceTypeResponseFilter> campSitePlaceTypeList;
    private List<UtilityResponseFilter> campSiteUtilityList;
    private List<CampTypeResponseFilter> campSiteCampTypeList;

}
