package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CampTypeUpdateRequest(

        @Schema(description = "Type of the camp", example = "Deluxe Tent")
        @NotBlank(message = "Type cannot be empty")
        String type,

        @Schema(description = "Maximum capacity of the camp", example = "4")
        @Min(value = 1, message = "Capacity must be greater than 0")
        int capacity,

        @Schema(description = "Base price of the camp", example = "100.0")
        @Positive(message = "Price must be a positive number")
        double price,

        @Schema(description = "Weekend price rate", example = "120.0")
        @Positive(message = "Weekend rate must be a positive number")
        double weekendRate,

        @Schema(description = "Holiday price rate", example = "150.0")
        @Positive(message = "Holiday rate must be a positive number")
        double holidayRate,

        @Schema(description = "Last update time", example = "2024-02-10T12:00:00")
        LocalDateTime updatedAt,

        @Schema(description = "Quantity available", example = "5")
        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,

        @Schema(description = "Camp type status (active/inactive)", example = "true")
        boolean status

//        @Schema(description = "Image file for the camp type")
//        MultipartFile file

) {
}
