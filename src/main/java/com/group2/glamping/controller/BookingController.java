package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.service.interfaces.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Booking API", description = "API for managing Bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(
            summary = "Create a new booking",
            description = "Create a new booking with booking details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    public ResponseEntity<BaseResponse> addBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        Optional<BookingResponse> booking = bookingService.createBooking(bookingRequest);
        return booking.map(response -> ResponseEntity.ok(BaseResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .message("Booking created successfully")
                        .build()))
                .orElse(ResponseEntity.badRequest().body(BaseResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Failed to create booking")
                        .data(null)
                        .build()));
    }
}
