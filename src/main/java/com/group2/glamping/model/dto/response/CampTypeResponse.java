package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@JsonFilter("dynamicFilter")
public class CampTypeResponse {
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
    Long availableSlot = (long) quantity;
    Double estimatedPrice;

    public static CampTypeResponse fromEntity(CampType campType, S3Service s3Service) {
        if (campType == null) {
            return null;
        }


        return CampTypeResponse.builder()
                .id(campType.getId())
                .type(campType.getType())
                .capacity(campType.getCapacity())
                .price(campType.getPrice())
                .weekendRate(campType.getWeekendRate())
                .updatedAt(campType.getUpdatedTime())
                .quantity(campType.getQuantity())
                .status(campType.isStatus())
                .image(s3Service.getFileUrl(campType.getImage()))
                .facilities(FacilityResponse.fromEntity(campType.getFacilities(), s3Service))
                .estimatedPrice(campType.getPrice())
                .build();
    }


}
