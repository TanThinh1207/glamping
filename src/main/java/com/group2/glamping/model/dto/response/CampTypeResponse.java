package com.group2.glamping.model.dto.response;

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

//  "id": 0,
//          "type": "sea",
//          "capacity": 0,
//          "price": 0,
//          "weekendRate": 0,
//          "holidayRate": 0,
//          "updatedTime": "2025-01-22T06:48:04.885Z",
//          "quantity": 0,
//          "status": true
}
