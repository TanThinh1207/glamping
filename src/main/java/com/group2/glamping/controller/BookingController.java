package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.service.interfaces.BookingDetailService;
import com.group2.glamping.service.interfaces.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking API", description = "API for managing Bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;
    private final BookingDetailService bookingDetailService;

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
    @PutMapping("/{bookingId}/update-status")
    @Operation(
            summary = "Update booking status",
            description = "Update booking status (Accepted, Denied, Checked-in, Checked-out) based on booking ID.",
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
            @RequestParam(required = false) String deniedReason
    ) {
        try {
            BookingResponse response = null;

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
                    response = bookingService.checkOutBooking(bookingId);
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

    // </editor-fold>

    // <editor-fold default state="collapsed" desc="Retrieve bookings by status">
    @PutMapping("/{bookingId}/comment")
    @Operation(
            summary = "Update booking comment",
            description = "Updates the comment of a booking by its ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JSON payload containing the new comment",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Example Request",
                                    value = "{ \"comment\": \"This is an updated comment.\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking comment updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"statusCode\": 200,\n" +
                                                    "  \"message\": \"Booking comment updated successfully\",\n" +
                                                    "  \"data\": {\n" +
                                                    "    \"bookingId\": 1,\n" +
                                                    "    \"comment\": \"This is an updated comment.\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request data",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{ \"statusCode\": 400, \"message\": \"Comment cannot be empty\", \"data\": null }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "No Booking found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{ \"statusCode\": 404, \"message\": \"No booking found with ID 1\", \"data\": null }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{ \"statusCode\": 500, \"message\": \"An unexpected error occurred. Please try again later.\", \"data\": null }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<BaseResponse> updateBookingComment(
            @PathVariable Integer bookingId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String comment = requestBody.get("comment");
            if (comment == null || comment.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(), "Comment cannot be empty", null));
            }
            BookingResponse response = bookingService.updateBookingComment(bookingId, comment);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Booking comment updated successfully", response));
        } catch (Exception e) {
            logger.error("Error while updating booking comment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // </editor-fold>

    // Retrieve by status
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
//    //Check in booking
//    @PutMapping("/{bookingDetailId}/check-in")
//    @Operation(
//            summary = "Checkin",
//            description = "Checkin by booking detail id.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Bookings accepted successfully"),
//                    @ApiResponse(responseCode = "404", description = "No Bookings found"),
//                    @ApiResponse(responseCode = "500", description = "Internal server error")
//            }
//    )
//    public ResponseEntity<BaseResponse> checkinBookingDetail(
//            @PathVariable Integer bookingDetailId
//    ) {
//        try {
//            BookingDetailResponse responses = bookingDetailService.checkInBookingDetail(bookingDetailId);
//            if (responses == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
//                                "No Booking Details found with Id: " + bookingDetailId, null));
//            }
//
//            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Checkin successfully", responses));
//        } catch (Exception e) {
//            logger.error("Error while retrieving bookings by status: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
//        }
//    }

    // <editor-fold default state="collapsed" desc="Accept or deny bookings (OLD VERSION)">
    //Accept bookings
//    @PutMapping("/{bookingId}/accept")
//    @Operation(
//            summary = "Retrieve bookings by status and Camp Site Id",
//            description = "Retrieves Booking records filtered by status (Pending, Completed, etc.) and Camp Site Id.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Bookings accepted successfully"),
//                    @ApiResponse(responseCode = "404", description = "No Bookings found"),
//                    @ApiResponse(responseCode = "500", description = "Internal server error")
//            }
//    )
//    public ResponseEntity<BaseResponse> acceptBookings(
//            @PathVariable Integer bookingId
//    ) {
//        try {
//            BookingResponse responses = bookingService.acceptBookings(bookingId);
//            if (responses == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
//                                "No Bookings found with Id: " + bookingId , null));
//            }
//
//            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Bookings accepted successfully", responses));
//        } catch (Exception e) {
//            logger.error("Error while retrieving bookings by status: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
//        }
//    }
//
//    //Deny bookings
//    @PutMapping("/{bookingId}/deny")
//    @Operation(
//            summary = "Retrieve bookings by status and Camp Site Id",
//            description = "Retrieves Booking records filtered by status (Pending, Completed, etc.) and Camp Site Id.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Bookings denied successfully"),
//                    @ApiResponse(responseCode = "404", description = "No Bookings found"),
//                    @ApiResponse(responseCode = "500", description = "Internal server error")
//            }
//    )
//    public ResponseEntity<BaseResponse> denyBookings(
//            @PathVariable Integer bookingId,
//            @RequestParam String deniedReason
//    ) {
//        try {
//            BookingResponse responses = bookingService.denyBookings(bookingId, deniedReason);
//            if (responses == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
//                                "No Bookings found with Id: " + bookingId , null));
//            }
//
//            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Bookings denied successfully", responses));
//        } catch (Exception e) {
//            logger.error("Error while retrieving bookings by status: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
//        }
//    }
    // </editor-fold>
}
