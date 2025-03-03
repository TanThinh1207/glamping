package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.model.enums.BookingDetailStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
@AllArgsConstructor
public class BookingDetailResponse {

    CampTypeResponse campTypeResponse;
    CampResponse campResponse;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime checkInAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime checkOutAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
