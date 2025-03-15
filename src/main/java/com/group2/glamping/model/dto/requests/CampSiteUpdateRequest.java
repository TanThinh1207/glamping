package com.group2.glamping.model.dto.requests;

import com.group2.glamping.model.enums.CampSiteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CampSiteUpdateRequest {

    @Schema(description = "The name of the campsite", example = "Sunset Campsite")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Schema(description = "The address of the campsite", example = "123 Forest Road, Green Valley")
    @Size(max = 200, message = "Address must be less than 200 characters")
    private String address;

    @Schema(description = "The city of the campsite", example = "Manchester")
    @Size(max = 200, message = "City must be less than 200 characters")
    private String city;

    @Schema(description = "The status of the campsite", example = "Pending")
    @NotNull(message = "Status cannot be null")
    private CampSiteStatus status;

    @Schema(description = "Latitude of the campsite location", example = "34.0522")
    private Double latitude;

    @Schema(description = "Longitude of the campsite location", example = "-118.2437")
    private Double longitude;

    @Schema(description = "Additional message about the campsite", example = "This site is temporarily closed for maintenance")
    private String message;

    @Schema(description = "Deposit rate for booking the campsite", example = "0.25")
    private Double depositRate; // Dùng Double thay vì double để có thể null

    @Schema(description = "Detailed description of the campsite", example = "A beautiful campsite with stunning sunset views and excellent facilities")
    private String description;

}
