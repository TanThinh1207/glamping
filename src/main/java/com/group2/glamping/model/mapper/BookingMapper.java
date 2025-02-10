package com.group2.glamping.model.mapper;

import com.group2.glamping.model.dto.response.BookingDetailResponse;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.model.dto.response.BookingSelectionResponse;
import com.group2.glamping.model.dto.response.UserResponse;
import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.model.entity.BookingSelection;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingResponse toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingResponse.builder()
                .id(booking.getId())
                .user(new UserResponse(booking.getUser()))
                .campSite(CampSiteMapper.toDto(booking.getCampSite()))
                .created_at(booking.getCreatedTime())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .bookingDetailResponseList(mapBookingDetails(booking.getBookingDetailList()))
                .bookingSelectionResponseList(mapBookingSelections(booking.getBookingSelectionList()))
                .build();
    }

    private static List<BookingDetailResponse> mapBookingDetails(List<BookingDetail> bookingDetails) {
        return (bookingDetails != null) ? bookingDetails.stream()
                .map(BookingDetailResponse::fromEntity)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    private static List<BookingSelectionResponse> mapBookingSelections(List<BookingSelection> bookingSelections) {
        return (bookingSelections != null) ? bookingSelections.stream()
                .map(BookingSelectionResponse::fromEntity)
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
