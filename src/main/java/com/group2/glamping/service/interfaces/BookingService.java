package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BookingDetailResponse;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.model.dto.response.PagingResponse;

import java.util.Map;
import java.util.Optional;

public interface BookingService {

    Optional<BookingResponse> createBooking(BookingRequest booking);

    BookingResponse acceptBookings(Integer campSiteId);

    BookingResponse denyBookings(Integer bookingId, String deniedReason);

    PagingResponse<?> getBookings(Map<String, String> params, int page, int size, String sortBy, String direction);

    Object getFilteredBookings(Map<String, String> params, int page, int size, String fields, String sortBy, String direction);

    void confirmPaymentSuccess(Integer orderId);

    BookingResponse checkInBooking(Integer bookingId);

    BookingResponse checkOutBooking(Integer bookingId);

    BookingResponse updateBookingComment(Integer bookingId, String comment);
}
