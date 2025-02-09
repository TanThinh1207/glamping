package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Schema(description = "Request DTO for creating or updating a PlaceType")
public record PlaceTypeRequest(


        @Schema(description = "ID of the PlaceType (optional for creation)", example = "1")
        @Min(value = 1, message = "ID must be a positive integer greater than 0")
        @Max(value = 999999, message = "ID must be less than 999999")
        @Pattern(regexp = "^[0-9]+$", message = "ID must contain only digits")
        Integer id,

        @Schema(description = "Name of the PlaceType", example = "WiFi")
        @Pattern(regexp = "^[a-zA-Z0-9\\s-_]{3,50}$", message = "Name must be between 3-50 characters and contain only letters, numbers, spaces, '-', or '_'")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        String name,

        @Schema(description = "Image file for the PlaceType (optional)")
        MultipartFile imagePath
) {
}
