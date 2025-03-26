package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record ReportRequest(

        @Schema(description = "ID of the Campsite that user what to send the report", example = "1")
        @Min(value = 1, message = "ID must be a positive integer greater than 0")
        @Max(value = 999999, message = "ID must be less than 999999")
        Integer campSiteId,

        @Schema(description = "ID of the user who creates the report", example = "1")
        @Min(value = 1, message = "ID must be a positive integer greater than 0")
        @Max(value = 999999, message = "ID must be less than 999999")
        Integer userId,

        @Schema(description = "Message of the report",
                example = "The air conditioner is broken.")
        @Pattern(regexp = "^[a-zA-Z0-9\\s-_]{3,255}$",
                message = "Name must be between 3-50 characters and contain only letters, numbers, spaces, '-', or '_'")
        String message,

        @Schema(description = "Report Type", example = "Report Type")
                String reportType

) {
}
