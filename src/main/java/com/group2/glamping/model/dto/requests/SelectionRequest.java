package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SelectionRequest(

        @Schema(description = "ID of the Selection (optional for creation)", example = "1")
        @Min(value = 1, message = "ID must be a positive integer greater than 0")
        @Max(value = 999999, message = "ID must be less than 999999")
        Integer id,

        @Schema(description = "Name of the Selection", example = "BBQ Setup")
        String name,

        @Schema(description = "Description of the Selection",
                example = "BBQ grill setup with charcoal, utensils, and seating for a great outdoor meal.")
        @Pattern(regexp = "^[a-zA-Z0-9\\s-_]{3,255}$",
                message = "Name must be between 3-50 characters and contain only letters, numbers, spaces, '-', or '_'")
        String description,

        @Schema(description = "Price of the Selection", example = "30.00")
        @Pattern(regexp = "^\\d{1,10}(\\.\\d{1,2})?$", message = "Price must be a valid decimal number with up to 10 digits and optionally 2 decimals.")
        double price,

        @Schema(description = "ID of the Campsite for attaching this selection", example = "1")
        @Min(value = 1, message = "ID must be a positive integer greater than 0")
        @Max(value = 999999, message = "ID must be less than 999999")
        Integer campSiteId
) {
}
