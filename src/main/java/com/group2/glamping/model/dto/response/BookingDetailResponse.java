package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.model.enums.BookingDetailStatus;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
@AllArgsConstructor
public class BookingDetailResponse {

    int bookingDetailId;
    CampTypeResponse campTypeResponse;
    CampResponse campResponse;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime checkInAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime checkOutAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    BookingDetailStatus status;

    public static BookingDetailResponse fromEntity(BookingDetail bookingDetail, S3Service s3Service) {
        if (bookingDetail == null) {
            return null;
        }

        return BookingDetailResponse.builder()
                .bookingDetailId(bookingDetail.getId())
                .campTypeResponse(CampTypeResponse.fromEntity(bookingDetail.getCampType(), s3Service))
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
