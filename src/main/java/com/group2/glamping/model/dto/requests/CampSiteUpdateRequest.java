package com.group2.glamping.model.dto.requests;

import com.group2.glamping.model.enums.CampSiteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record CampSiteUpdateRequest(
        @Schema(description = "The name of the campsite", example = "Sunset Campsite")
        @NotBlank(message = "Name cannot be blank")
        @Size(max = 100, message = "Name must be less than 100 characters")
        String name,

        @Schema(description = "The address of the campsite", example = "123 Forest Road, Green Valley")
        @NotBlank(message = "Address cannot be blank")
        @Size(max = 200, message = "Address must be less than 200 characters")
        String address,

        @Schema(description = "The status of the campsite", example = "Pending")
        @NotNull(message = "Status cannot be null")
        CampSiteStatus status
) {
}




