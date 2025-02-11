package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingSelectionRequest(

        @Schema(description = "ID of the booking", example = "1")
        @NotNull(message = "Booking ID cannot be null")
        @Min(value = 1, message = "Booking ID must be a positive number")
        int idBooking,

        @Schema(description = "Quantity of selections", example = "2")
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity

) {
}
