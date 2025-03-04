package com.group2.glamping.model.dto.requests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampTypeCreateRequest {

    String type;
    int capacity;
    double price;
    double weekendRate;
    int quantity;
    int campSiteId;
    List<Integer> facilities;
}

