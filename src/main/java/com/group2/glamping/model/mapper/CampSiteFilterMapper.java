package com.group2.glamping.model.mapper;

import com.group2.glamping.model.dto.response.ImageResponse;
import com.group2.glamping.model.dto.response.filter.CampSiteResponseFilter;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CampSiteFilterMapper {

    private final S3Service s3Service;

    public CampSiteResponseFilter toDto(CampSite campSite) {
        if (campSite == null) {
            return null;
        }
        return CampSiteResponseFilter.builder()
                .id(campSite.getId())
                .name(campSite.getName())
                .address(campSite.getAddress())
                .city(campSite.getCity())
                .latitude(campSite.getLatitude())
                .longitude(campSite.getLongitude())
                .createdTime(campSite.getCreatedTime())
                .status(campSite.getStatus())
                .message(campSite.getMessage())
                .depositRate(campSite.getDepositRate())
                .imageList(mapImages(campSite))
                .description(campSite.getDescription())
                .build();
    }

    private List<ImageResponse> mapImages(CampSite campSite) {
        return (campSite.getImageList() != null) ?
                campSite.getImageList().stream()
                        .map(image -> new ImageResponse(image.getId(), s3Service.generatePresignedUrl(image.getPath())))
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }


}
