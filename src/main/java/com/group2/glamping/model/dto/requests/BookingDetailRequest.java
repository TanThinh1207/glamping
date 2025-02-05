package com.group2.glamping.model.dto.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingDetailRequest {

    int campTypeId;
    int quantity;
    LocalDateTime checkInAt;
    LocalDateTime checkOutAt;

}
