package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request DTO for creating payment")
public record PaymentRequest(
        @Schema(description = "The amount to be paid", example = "100.50")
        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be a positive value")
        double amount,

        @Schema(description = "The currency of the payment", example = "vnd")
        @NotBlank(message = "Currency cannot be blank")
        @Size(min = 3, max = 3, message = "Currency must be a 3-character code")
        String currency,

        @Schema(description = "The name of the booking", example = "Sa Đéc Glamping")
        @NotBlank(message = "Name cannot be blank")
        @Size(max = 100, message = "Name must be less than 100 characters")
        String name,

        @Schema(description = "The ID of the booking associated with the payment", example = "1")
        @NotNull(message = "Booking ID cannot be null")
        @Positive(message = "Booking ID must be a positive value")
        int bookingId
) {
}