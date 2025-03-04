package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public record CampSiteRequest(

        @Schema(description = "ID of the host", example = "101")
        @Min(value = 1, message = "Host ID must be a positive integer greater than 0")
        int hostId,

        @Schema(description = "Name of the campsite", example = "Mountain View Camp")
        @NotBlank(message = "Camp site name cannot be empty")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

        @Schema(description = "Address of the campsite", example = "123 Forest Road, Green Valley")
        @NotBlank(message = "Address cannot be empty")
        String address,

        @Schema(description = "Latitude of the campsite", example = "37.7749")
        @DecimalMin(value = "-90.0", message = "Latitude must be greater than -90")
        @DecimalMax(value = "90.0", message = "Latitude must be less than 90")
        double latitude,

        @Schema(description = "Longitude of the campsite", example = "-122.4194")
        @DecimalMin(value = "-180.0", message = "Longitude must be greater than -180")
        @DecimalMax(value = "180.0", message = "Longitude must be less than 180")
        double longitude,

        @Schema(description = "City where the campsite is located", example = "San Francisco")
        @NotBlank(message = "City cannot be empty")
        String city,

        @Schema(description = "List of place types id associated with the campsite")
        List<Integer> placeTypeIds,

        @Schema(description = "List of selections associated with the campsite")
        List<SelectionRequest> campSiteSelections,

        @Schema(description = "List of utilities provided at the campsite")
        List<Integer> utilityIds,

        @Schema(description = "List of camp types associated with the campsite")
        List<CampTypeUpdateRequest> campTypeList,

        @Schema(description = "List of facility ids")
        List<Integer> facilityIds

) {
}
