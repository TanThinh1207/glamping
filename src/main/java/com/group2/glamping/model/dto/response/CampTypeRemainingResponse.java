package com.group2.glamping.model.dto.response;

import lombok.Data;

@Data
public class CampTypeRemainingResponse {
    private Integer campTypeId;
    private String type;
    private Integer capacity;
    private Long remainingQuantity;

}