package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Schema(description = "Request DTO for creating or updating a utility")
public record UtilityRequest(


        @Schema(description = "ID of the utility (optional for creation)", example = "1")
        Integer id,

        @Schema(description = "Name of the utility", example = "WiFi")
        String name,

        @Schema(description = "Image file for the utility (optional)")
        MultipartFile imagePath
) {
}
