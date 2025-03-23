package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.BookingDetailOrderRequest;
import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.entity.Booking;
import com.stripe.exception.StripeException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingService {

    Optional<BookingResponse> createBooking(BookingRequest booking) throws StripeException;

    BookingResponse acceptBookings(Integer campSiteId) throws StripeException;

    BookingResponse denyBookings(Integer bookingId, String deniedReason) throws StripeException;

    PagingResponse<?> getBookings(Map<String, String> params, int page, int size, String sortBy, String direction);

    Object getFilteredBookings(Map<String, String> params, int page, int size, String fields, String sortBy, String direction);

    BookingResponse checkInBooking(Integer bookingId) throws StripeException;

    BookingResponse checkOutBooking(Integer bookingId, List<BookingDetailOrderRequest> bookingDetailOrderRequest) throws StripeException, IOException;

    Booking getBookingById(Integer bookingId);

    BookingResponse ratingBooking(Integer bookingId, Integer rating, String comment) throws StripeException;
}
