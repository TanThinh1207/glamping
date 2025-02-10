package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

public record FacilityRequest(

        @Schema(description = "ID of the Facility (optional for creation)", example = "1")
        @Min(value = 1, message = "ID must be a positive integer greater than 0")
        @Max(value = 999999, message = "ID must be less than 999999")
        Integer id,

        @Schema(description = "Name of the Facility", example = "WiFi")
        String name,

        @Schema(description = "Description of the Facility", example = "High-speed internet access")
        @Pattern(regexp = "^[a-zA-Z0-9\\s-_]{3,255}$", message = "Name must be between 3-50 characters and contain only letters, numbers, spaces, '-', or '_'")
        String description
//        MultipartFile image
) {
}
