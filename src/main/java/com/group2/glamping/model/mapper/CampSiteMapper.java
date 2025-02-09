package com.group2.glamping.model.mapper;

import com.group2.glamping.model.dto.response.*;
import com.group2.glamping.model.entity.CampSite;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CampSiteMapper {

    public static CampSiteResponse toDto(CampSite campSite) {
        if (campSite == null) {
            return null;
        }

        return CampSiteResponse.builder()
                .id(campSite.getId())
                .name(campSite.getName())
                .address(campSite.getAddress())
                .latitude(campSite.getLatitude())
                .longitude(campSite.getLongitude())
                .createdTime(campSite.getCreatedTime())
                .status(campSite.getStatus())
                .imageList(mapImages(campSite))
                .campSiteSelectionsList(mapSelections(campSite))
                .campSiteUtilityList(mapUtilities(campSite))
                .campSitePlaceTypeList(mapPlaceTypes(campSite))
                .campSiteCampTypeList(mapCampType(campSite))
                .build();
    }

    private static List<ImageResponse> mapImages(CampSite campSite) {
        return (campSite.getImageList() != null) ?
                campSite.getImageList().stream()
                        .map(image -> new ImageResponse(image.getId(), image.getPath()))
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    private static List<SelectionResponse> mapSelections(CampSite campSite) {
        return (campSite.getSelections() != null) ?
                campSite.getSelections().stream()
                        .map(selection -> {
                            if (selection != null) {
                                return new SelectionResponse(
                                        selection.getId(),
                                        selection.getName(),
                                        selection.getDescription(),
                                        selection.getPrice(),
                                        selection.getImageUrl(),
                                        selection.isStatus()
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }


    private static List<UtilityResponse> mapUtilities(CampSite campSite) {
        return (campSite.getUtilities() != null) ?
                campSite.getUtilities().stream()
                        .map(utility -> {
                            if (utility != null) {
                                return new UtilityResponse(
                                        utility.getId(),
                                        utility.getName(),
                                        utility.getImageUrl(),
                                        utility.isStatus()
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    private static List<PlaceTypeResponse> mapPlaceTypes(CampSite campSite) {
        return (campSite.getPlaceTypes() != null) ?
                campSite.getPlaceTypes().stream()
                        .map(placeType -> {
                            if (placeType != null) {
                                return new PlaceTypeResponse(
                                        placeType.getId(),
                                        placeType.getName(),
                                        placeType.getImage(),
                                        placeType.isStatus()
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    private static List<CampTypeResponse> mapCampType(CampSite campSite) {
        return (campSite.getCampTypes() != null) ?
                campSite.getCampTypes().stream()
                        .map(placeType -> {
                            if (placeType != null) {
                                return new CampTypeResponse(
                                        placeType.getId(),
                                        placeType.getType(),
                                        placeType.getCapacity(),
                                        placeType.getPrice(),
                                        placeType.getWeekendRate(),
                                        placeType.getHolidayRate(),
                                        LocalDateTime.now(),
                                        placeType.getQuantity(),
                                        placeType.isStatus()
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }
}
