package com.group2.glamping.model.dto.requests;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookingSelectionRequest {
    int id_service;
    int quantity;
}
