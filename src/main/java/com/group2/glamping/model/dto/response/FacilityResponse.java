package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.group2.glamping.model.entity.Facility;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("dynamicFilter")
public class FacilityResponse {
    private int id;
    private String name;
    private String description;
    private String image;
    private boolean status;

    public static List<FacilityResponse> fromEntity(List<Facility> facility, S3Service s3Service) {

        if (facility == null) {
            return null;
        }

        return facility.stream()
                .map(facility1 -> FacilityResponse.builder()
                        .id(facility1.getId())
                        .name(facility1.getDescription())
                        .description(facility1.getDescription())
                        .image(s3Service.getFileUrl(facility1.getImageUrl()))
                        .status(facility1.isStatus())
                        .build()).toList();
    }

}
