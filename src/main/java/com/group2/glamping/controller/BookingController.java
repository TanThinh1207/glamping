package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.service.interfaces.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking API", description = "API for managing Bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
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

    // Retrieve Booking with Pending Status
    @GetMapping("/getPendingBooking")
    @Operation(
            summary = "Retrieve  types with Pending Status and Camp Site Id",
            description = "Retrieves Bookings types filtered by status (Pending) and Camp Site Id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "No Booking types found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> getPendingBooking(
            @RequestParam(required = true) Integer campSiteId
    ) {
        try {
            List<BookingResponse> responses = bookingService.getPendingBookingsByCampSiteId(campSiteId);

            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "No Pending Bookings found with Camp Site Id: " + campSiteId , responses));
            }

            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Pending Bookings retrieved successfully", responses));
        } catch (Exception e) {
            logger.error("Error while retrieving place types by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Retrieve Booking with Completed Booking
    @GetMapping("/getCompletedBooking")
    @Operation(
            summary = "Retrieve  types with Pending Status and Camp Site Id",
            description = "Retrieves Bookings types filtered by status (Pending) and Camp Site Id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "No Booking types found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> getCompletedBooking(
            @RequestParam(required = true) Integer campSiteId
    ) {
        try {
            List<BookingResponse> responses = bookingService.getCompletedBookingsByCampSiteId(campSiteId);

            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "No Completed Bookings found with Camp Site Id: " + campSiteId , responses));
            }

            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Pending Bookings retrieved successfully", responses));
        } catch (Exception e) {
            logger.error("Error while retrieving place types by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

}
