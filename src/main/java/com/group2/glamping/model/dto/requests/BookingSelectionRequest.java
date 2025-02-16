package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingSelectionRequest(

        @Schema(description = "ID of the selection", example = "1")
        @NotNull(message = "Selection ID cannot be null")
        @Min(value = 1, message = "Selection ID must be a positive number")
        int idSelection,

        @Schema(description = "Quantity of selections", example = "2")
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity

) {
}
