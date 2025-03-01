package com.group2.glamping.model.dto.requests;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingRequest {

    List<BookingDetailRequest> bookingDetails;
    int userId;
    int campSiteId;
    double totalAmount;
    LocalDateTime checkInTime;
    LocalDateTime checkOutTime;
    List<BookingSelectionRequest> bookingSelectionRequestList;

}
