package com.group2.glamping.controller;


import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.service.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    ResponseEntity<?> addBooking(@RequestBody BookingRequest bookingRequest) {
        Optional<Booking> booking = bookingService.createBooking(bookingRequest);
        if (booking.isPresent()) {
            return ResponseEntity.ok(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .data(booking)
                    .message("OK")
                    .build());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


}
