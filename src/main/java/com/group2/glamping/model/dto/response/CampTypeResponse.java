package com.group2.glamping.model.dto.response;

import com.group2.glamping.model.entity.CampType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampTypeResponse {
    int id;
    String type;
    int capacity;
    double price;
    double weekendRate;
    double holidayRate;
    LocalDateTime updatedAt;
    int quantity;
    boolean status;

    public static CampTypeResponse fromEntity(CampType campType) {
        if (campType == null) {
            return null;
        }

        return CampTypeResponse.builder()
                .id(campType.getId())
                .type(campType.getType())
                .capacity(campType.getCapacity())
                .price(campType.getPrice())
                .weekendRate(campType.getWeekendRate())
                .holidayRate(campType.getHolidayRate())
                .updatedAt(campType.getUpdatedTime())
                .quantity(campType.getQuantity())
                .status(campType.isStatus())
                .build();
    }
}
