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
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
                    @ApiResponse(responseCode = "200", description = "Campsites retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @GetMapping
    public ResponseEntity<MappingJacksonValue> getCampSites(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields) {
        return ResponseEntity.ok(bookingService.getFilteredBookings(params, page, size, fields));
    }

//    //Retrieve by status
//    @GetMapping
//    @Operation(
//            summary = "Retrieve bookings by status and Camp Site Id",
//            description = "Retrieves Booking records filtered by status (Pending, Completed, etc.) and Camp Site Id.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
//                    @ApiResponse(responseCode = "404", description = "No Bookings found"),
//                    @ApiResponse(responseCode = "500", description = "Internal server error")
//            }
//    )
//    public ResponseEntity<BaseResponse> getBookings(
//            @RequestParam Integer campSiteId,
//            @RequestParam(required = false) String status
//    ) {
//        try {
//            List<BookingResponse> responses = new ArrayList<>();
//            if (status.equals("pending")) {
//                responses = bookingService.getPendingBookingsByCampSiteId(campSiteId);
//            } else if (status.equals("completed")) {
//                responses = bookingService.getCompletedBookingsByCampSiteId(campSiteId);
//            }
//            if (responses.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
//                                "No Bookings found with Camp Site Id: " + campSiteId + " and status: " + status, responses));
//            }
//
//            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Bookings retrieved successfully", responses));
//        } catch (Exception e) {
//            logger.error("Error while retrieving bookings by status: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
//        }
//    }

       //Accept bookings
    @PutMapping("/{bookingId}/accept")
    @Operation(
            summary = "Retrieve bookings by status and Camp Site Id",
            description = "Retrieves Booking records filtered by status (Pending, Completed, etc.) and Camp Site Id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bookings accepted successfully"),
                    @ApiResponse(responseCode = "404", description = "No Bookings found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> acceptBookings(
            @PathVariable Integer bookingId
    ) {
        try {
            BookingResponse responses = bookingService.acceptBookings(bookingId);
            if (responses == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
                                "No Bookings found with Id: " + bookingId , null));
            }

            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Bookings accepted successfully", responses));
        } catch (Exception e) {
            logger.error("Error while retrieving bookings by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    //Deny bookings
    @PutMapping("/{bookingId}/deny")
    @Operation(
            summary = "Retrieve bookings by status and Camp Site Id",
            description = "Retrieves Booking records filtered by status (Pending, Completed, etc.) and Camp Site Id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bookings denied successfully"),
                    @ApiResponse(responseCode = "404", description = "No Bookings found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> denyBookings(
            @PathVariable Integer bookingId,
            @RequestParam String deniedReason
    ) {
        try {
            BookingResponse responses = bookingService.denyBookings(bookingId, deniedReason);
            if (responses == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
                                "No Bookings found with Id: " + bookingId , null));
            }

            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Bookings denied successfully", responses));
        } catch (Exception e) {
            logger.error("Error while retrieving bookings by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
}
