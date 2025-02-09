package com.group2.glamping.model.dto.response;

import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.model.enums.BookingDetailStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Value
@AllArgsConstructor
public class BookingDetailResponse {

    CampTypeResponse campTypeResponse;
    CampResponse campResponse;
    LocalDateTime checkInAt;
    LocalDateTime checkOutAt;
    LocalDateTime createdAt;
    BookingDetailStatus status;

    public static BookingDetailResponse fromEntity(BookingDetail bookingDetail) {
        if (bookingDetail == null) {
            return null;
        }

        return BookingDetailResponse.builder()
                .campTypeResponse(CampTypeResponse.fromEntity(bookingDetail.getCampType()))
                .campResponse(CampResponse.fromEntity(bookingDetail.getCamp()))
                .checkInAt(bookingDetail.getCheckInTime())
                .checkOutAt(bookingDetail.getCheckOutTime())
                .createdAt(bookingDetail.getCreatedTime())
                .status(bookingDetail.getStatus())
                .build();
    }
//
//    public static List<BookingDetailResponse> fromEntity(List<BookingDetail> bookingDetails) {
//        if (bookingDetails == null || bookingDetails.isEmpty()) {
//            return List.of();
//        }
//
//        return bookingDetails.stream()
//                .map(BookingDetailResponse::fromEntity)
//                .collect(Collectors.toList());
//    }
}
