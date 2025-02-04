package com.group2.glamping.model.dto.requests;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookingServiceRequest {
    int id_service;
    int quantity;
}
