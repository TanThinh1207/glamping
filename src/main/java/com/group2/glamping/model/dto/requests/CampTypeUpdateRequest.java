package com.group2.glamping.model.dto.requests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    LocalDateTime updatedAt;
    int quantity;
    boolean status;
}

