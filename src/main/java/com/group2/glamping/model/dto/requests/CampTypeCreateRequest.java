package com.group2.glamping.model.dto.requests;

import com.group2.glamping.model.entity.CampSite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampTypeCreateRequest {

    String type;
    int capacity;
    double price;
    double weekendRate;
    double holidayRate;
    int quantity;
    int campSiteId;
}
