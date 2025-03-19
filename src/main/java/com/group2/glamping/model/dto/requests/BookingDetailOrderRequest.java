package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for adding order details during booking checkout")
public record BookingDetailOrderRequest(
        @Schema(description = "The ID of the booking detail", example = "1")
        int bookingDetailId,

        @Schema(description = "Name of the ordered item", example = "Coca-Cola")
        String name,

        @Schema(description = "Quantity of the ordered item", example = "3")
        long quantity,

        @Schema(description = "Price per unit of the ordered item", example = "100.0")
        Double price,

        @Schema(description = "Total amount for the ordered items = quantity * price", example = "300.0")
        Double totalAmount,

        @Schema(description = "Additional notes for the order", example = "No ice")
        String note
) {
}
