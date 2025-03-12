package com.group2.glamping.model.mapper;

import com.group2.glamping.model.dto.response.*;
import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.model.entity.BookingSelection;
import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final CampSiteMapper campSiteMapper;
    private final S3Service s3Service;

    public BookingResponse toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingResponse.builder()
                .id(booking.getId())
                .user(new UserResponse(booking.getUser()))
                .campSite(campSiteMapper.toDto(booking.getCampSite()))
                .checkIn(booking.getCheckInTime())
                .checkOut(booking.getCheckOutTime())
                .createdAt(booking.getCreatedTime())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .bookingDetailResponseList(mapBookingDetails(booking.getBookingDetailList()))
                .bookingSelectionResponseList(mapBookingSelections(booking.getBookingSelectionList()))
                .paymentResponseList(mapPayments(booking.getPaymentList()))
                .build();
    }

    private List<BookingDetailResponse> mapBookingDetails(List<BookingDetail> bookingDetails) {
        return (bookingDetails != null) ? bookingDetails.stream()
                .map(detail -> BookingDetailResponse.fromEntity(detail, s3Service))
                .collect(Collectors.toList()) : Collections.emptyList();
    }


    private List<BookingSelectionResponse> mapBookingSelections(List<BookingSelection> bookingSelections) {
        return (bookingSelections != null) ? bookingSelections.stream()
                .map(BookingSelectionResponse::fromEntity)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    private List<PaymentResponse> mapPayments(List<Payment> payments) {
        return (payments != null) ? payments.stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
