package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingService {

    Optional<BookingResponse> createBooking(BookingRequest booking);

    List<BookingResponse> getPendingBookingsByCampSiteId(Integer campSiteId);

    List<BookingResponse> getCompletedBookingsByCampSiteId(Integer campSiteId);

    BookingResponse getBookingById(Integer bookingId);

    BookingResponse acceptBookings(Integer campSiteId);

    BookingResponse denyBookings(Integer bookingId, String deniedReason);

    PagingResponse<?> getBookings(Map<String, String> params, int page, int size);

    MappingJacksonValue getFilteredBookings(Map<String, String> params, int page, int size, String fields);

    ;

    void confirmPaymentSuccess(Integer orderId);

}
