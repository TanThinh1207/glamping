package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
@Schema(description = "Request DTO for creating or updating a PlaceType")
public record PlaceTypeRequest(


        @Schema(description = "ID of the PlaceType (optional for creation)", example = "1")
        @Min(value = 1, message = "ID must be a positive integer greater than 0")
        @Max(value = 999999, message = "ID must be less than 999999")
        Integer id,

        @Schema(description = "Name of the PlaceType", example = "Lake View")
        String name

) {
}
