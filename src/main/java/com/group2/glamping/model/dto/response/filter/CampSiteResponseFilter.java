package com.group2.glamping.model.dto.response.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.dto.response.ImageResponse;
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
public class CampSiteResponseFilter {

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
    private List<ImageResponse> imageList;
    private Integer hostId;

}
