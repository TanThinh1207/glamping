package com.group2.glamping.controller;

import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.BookingDetailResponse;
import com.group2.glamping.service.interfaces.BookingDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking-details")
@Tag(name = "Booking detail API", description = "API for managing booking details")
@RequiredArgsConstructor
public class BookingDetailController {
    private static final Logger logger = LoggerFactory.getLogger(BookingDetailController.class);
    private final BookingDetailService bookingDetailService;

    // <editor-fold default state="collapsed" desc="Assign camp">
    @PutMapping("/{bookingDetailId}/assign-camp")
    @Operation(
            summary = "Assign Camp to Booking Detail",
            description = "Assign a camp to a specific booking detail.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp assigned successfully"),
                    @ApiResponse(responseCode = "404", description = "No booking detail or camp found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> assignCampToBookingDetail(
            @PathVariable Integer bookingDetailId,
            @RequestParam Integer campId
    ) {
        try {
            BookingDetailResponse response = bookingDetailService.assignCamp(bookingDetailId, campId);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
                                "No Booking Details or Camp found with provided IDs", null));
            }

            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp assigned successfully", response));
        } catch (Exception e) {
            logger.error("Error while assigning camp {} to booking detail {}: {}", campId, bookingDetailId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "An unexpected error occurred. Please try again later.", null));
        }
    }

    // </editor-fold>
}
