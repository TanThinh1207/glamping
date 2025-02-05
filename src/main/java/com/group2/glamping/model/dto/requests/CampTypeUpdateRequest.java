package com.group2.glamping.model.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampTypeUpdateRequest {
    String type;
    int capacity;
    double price;
    double weekendRate;
    double holidayRate;
    int quantity;
    int campSiteId;
    boolean status;
}