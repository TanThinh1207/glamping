package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BookingResponse;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Optional<BookingResponse> createBooking(BookingRequest booking);

    List<BookingResponse> getPendingBookingsByCampSiteId(Integer campSiteId);

    List<BookingResponse> getCompletedBookingsByCampSiteId(Integer campSiteId);
}
