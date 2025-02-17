package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record PaymentRequest(
        @Schema(description = "The date of the payment", example = "2023-10-01T12:00:00Z")
        @NotNull(message = "Payment date cannot be null")
        Date paymentDate,

        @Schema(description = "The method used for payment", example = "Credit Card")
        @NotBlank(message = "Payment method cannot be blank")
        @Size(max = 50, message = "Payment method must be less than 50 characters")
        String paymentMethod,

        @Schema(description = "The status of the payment", example = "Completed")
        @NotBlank(message = "Payment status cannot be blank")
        @Size(max = 20, message = "Payment status must be less than 20 characters")
        String paymentStatus,

        @Schema(description = "The amount paid", example = "100.0")
        @NotNull(message = "Amount paid cannot be null")
        @PositiveOrZero(message = "Amount paid must be positive or zero")
        Double amountPaid,

        @Schema(description = "The total amount to be paid", example = "100.0")
        @NotNull(message = "Total amount cannot be null")
        @PositiveOrZero(message = "Total amount must be positive or zero")
        Double amountTotal
) {
}