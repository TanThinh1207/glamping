package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.BookingDetailOrderRequest;
import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.service.interfaces.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking API", description = "API for managing Bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    // <editor-fold default state="collapsed" desc="Create Booking">
    @PostMapping
    @Operation(
            summary = "Create a new booking",
            description = "Create a new booking with booking details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Booking created successfully"),
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


    // Retrieve Bookings
    @Operation(
            summary = "Get list of bookings",
            description = "Retrieve a paginated list of bookings with optional filtering and field selection",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(bookingService.getFilteredBookings(params, page, size, fields, sortBy, direction));
    }
    // </editor-fold>

    // <editor-fold default state="collapsed" desc="Accept or deny bookings">
    @PutMapping("/{bookingId}")
    @Operation(
            summary = "Update booking status",
            description = """
                     Update the status of a booking based on its ID. The possible statuses are:
                     - `accept`: Approve the booking request.
                     - `deny`: Reject the booking request (requires a denial reason).
                     - `checkin`: Mark the booking as checked-in.
                     - `checkout`: Mark the booking as checked-out (requires booking detail orders).
                                    \s
                     **Note:** \s
                     - When using `deny`, the `deniedReason` parameter is **required**. \s
                     - When using `checkout`, a list of `BookingDetailOrderRequest` must be provided in the request body.
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking status updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "No Booking found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> updateBookingStatus(
            @PathVariable Integer bookingId,
            @RequestParam String status,
            @RequestParam(required = false) String deniedReason,
            @RequestBody(required = false) List<BookingDetailOrderRequest> bookingDetailOrderRequest
    ) {
        try {
            BookingResponse response;

            switch (status.toLowerCase()) {
                case "accept":
                    response = bookingService.acceptBookings(bookingId);
                    break;
                case "deny":
                    if (deniedReason == null || deniedReason.isBlank()) {
                        return ResponseEntity.badRequest()
                                .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(),
                                        "Denied reason is required when setting status to Denied", null));
                    }
                    response = bookingService.denyBookings(bookingId, deniedReason);
                    break;
                case "checkin":
                    response = bookingService.checkInBooking(bookingId);
                    break;
                case "checkout":
                    response = bookingService.checkOutBooking(bookingId, bookingDetailOrderRequest);
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(),
                                    "Invalid status. Only 'accept', 'deny', 'checkin', or 'checkout' are allowed.", null));
            }

            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
                    "Booking status updated successfully", response));
        } catch (Exception e) {
            logger.error("Error while updating booking status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "An unexpected error occurred. Please try again later.", null));
        }
    }

}
