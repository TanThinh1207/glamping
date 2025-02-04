package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.entity.Booking;

import java.util.Optional;

public interface BookingService {

    Optional<Booking> createBooking(BookingRequest booking);


}
