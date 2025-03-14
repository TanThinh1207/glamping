package com.group2.glamping.model.dto.response.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampTypeResponseFilter {
    int id;
    String type;
    int capacity;
    double price;
    double weekendRate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
    int quantity;
    boolean status;
    int campSiteId;
    String image;
    List<FacilityResponse> facilities;

    public static CampTypeResponseFilter fromEntity(CampType campType, S3Service s3Service) {
        if (campType == null) {
            return null;
        }

        try {
            return CampTypeResponseFilter.builder()
                    .id(campType.getId())
                    .type(campType.getType())
                    .capacity(campType.getCapacity())
                    .price(campType.getPrice())
                    .weekendRate(campType.getWeekendRate())
                    .updatedAt(campType.getUpdatedTime())
                    .quantity(campType.getQuantity())
                    .status(campType.isStatus())
                    .image("https://d16irpmj68i9v1.cloudfront.net/" + campType.getImage())
                    .facilities(campType.getFacilities() != null ? FacilityResponse.fromEntity(campType.getFacilities(), s3Service) : List.of())
                    .build();
        } catch (Exception e) {
            // Log the error
            System.err.println("Error mapping CampType to CampTypeResponseFilter: " + e.getMessage());
            return null;
        }
    }
}
