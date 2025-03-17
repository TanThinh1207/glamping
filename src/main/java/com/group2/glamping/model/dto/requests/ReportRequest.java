package com.group2.glamping.model.dto.requests;

import com.group2.glamping.model.enums.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Schema(description = "Request DTO for creating or updating a Report")
public record ReportRequest(

        @Schema(description = "ID of the Report (optional for creation)", example = "1")
        @Min(value = 1, message = "ID must be a positive integer greater than 0")
        @Max(value = 999999, message = "ID must be less than 999999")
        Integer id,

        @Schema(description = "ID of the Campsite related to the report", example = "1")
        @NotNull(message = "CampSite ID cannot be null")
        @Min(value = 1, message = "CampSite ID must be a positive integer greater than 0")
        Integer campSiteId,

        @Schema(description = "ID of the User creating the report", example = "10")
        @NotNull(message = "User ID cannot be null")
        @Min(value = 1, message = "User ID must be a positive integer greater than 0")
        Integer userId,

        @Schema(description = "Status of the Report", example = "PENDING")
        @NotNull(message = "Status cannot be null")
        ReportStatus status,

        @Schema(description = "Timestamp when the report was created", example = "2025-03-13T12:00:00")
        LocalDateTime createdTime,

        @Schema(description = "Detailed message regarding the report", example = "The campsite was not clean upon arrival.")
        @NotNull(message = "Message cannot be null")
        @Pattern(regexp = "^[a-zA-Z0-9\\s-_.,!?]{3,255}$",
                message = "Message must be between 3-255 characters and contain only letters, numbers, spaces, and punctuation")
        String message,

        @Schema(description = "Type of report", example = "Hygiene Issue")
        @NotNull(message = "Report type cannot be null")
        @Pattern(regexp = "^[a-zA-Z0-9\\s-_]{3,50}$",
                message = "Report type must be between 3-50 characters and contain only letters, numbers, spaces, '-', or '_'")
        String reportType
) {
}
