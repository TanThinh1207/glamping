package com.group2.glamping.model.mapper;

import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.dto.response.CampSiteSelectionResponse;
import com.group2.glamping.model.dto.response.ImageResponse;
import com.group2.glamping.model.entity.CampSite;

import java.util.stream.Collectors;

public class CampSiteMapper {

    public static CampSiteResponse toDto(CampSite campSite) {
        return CampSiteResponse.builder()
                .id(campSite.getId())
                .name(campSite.getName())
                .address(campSite.getAddress())
                .latitude(campSite.getLatitude())
                .longitude(campSite.getLongitude())
                .createdTime(campSite.getCreatedTime())
                .status(campSite.getStatus())
                .imageList(campSite.getImageList().stream()
                        .map(image -> new ImageResponse(image.getId(), image.getPath()))
                        .collect(Collectors.toList()))
                .build();
    }
}



