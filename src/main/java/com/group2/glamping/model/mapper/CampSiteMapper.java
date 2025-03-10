package com.group2.glamping.model.mapper;

import com.group2.glamping.model.dto.response.*;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CampSiteMapper {

    private final S3Service s3Service;

    public CampSiteResponse toDto(CampSite campSite) {
        if (campSite == null) {
            return null;
        }

        return CampSiteResponse.builder()
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
                .campSiteSelectionsList(mapSelections(campSite))
                .campSiteUtilityList(mapUtilities(campSite))
                .campSitePlaceTypeList(mapPlaceTypes(campSite))
                .campSiteCampTypeList(mapCampType(campSite))
                .build();
    }

    private List<ImageResponse> mapImages(CampSite campSite) {
        return (campSite.getImageList() != null) ?
                campSite.getImageList().stream()
                        .map(image -> new ImageResponse(image.getId(), s3Service.generatePresignedUrl(image.getPath())))
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    private List<SelectionResponse> mapSelections(CampSite campSite) {

        return (campSite.getSelections() != null) ?
                campSite.getSelections().stream()
                        .map(selection -> {
                            if (selection != null) {
                                return new SelectionResponse(
                                        selection.getId(),
                                        selection.getName(),
                                        selection.getDescription(),
                                        selection.getPrice(),
                                        s3Service.generatePresignedUrl(selection.getImageUrl()),
                                        selection.isStatus()
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }


    private List<UtilityResponse> mapUtilities(CampSite campSite) {
        return (campSite.getUtilities() != null) ?
                campSite.getUtilities().stream()
                        .map(utility -> {
                            if (utility != null) {
                                return new UtilityResponse(
                                        utility.getId(),
                                        utility.getName(),
                                        s3Service.generatePresignedUrl(utility.getImageUrl()),
                                        utility.isStatus()
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    private List<PlaceTypeResponse> mapPlaceTypes(CampSite campSite) {
        return (campSite.getPlaceTypes() != null) ?
                campSite.getPlaceTypes().stream()
                        .map(placeType -> {
                            if (placeType != null) {
                                return new PlaceTypeResponse(
                                        placeType.getId(),
                                        placeType.getName(),
                                        s3Service.generatePresignedUrl(placeType.getImage()),
                                        placeType.isStatus()
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    private List<CampTypeResponse> mapCampType(CampSite campSite) {
        return (campSite.getCampTypes() != null) ?
                campSite.getCampTypes().stream()
                        .map(campType -> {
                            if (campType != null) {
                                return new CampTypeResponse(
                                        campType.getId(),
                                        campType.getType(),
                                        campType.getCapacity(),
                                        campType.getPrice(),
                                        campType.getWeekendRate(),
                                        LocalDateTime.now(),
                                        campType.getQuantity(),
                                        campType.isStatus(),
                                        campSite.getId(),
                                        campType.getImage(),
                                        FacilityResponse.fromEntity(campType.getFacilities(), s3Service)
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }
}
