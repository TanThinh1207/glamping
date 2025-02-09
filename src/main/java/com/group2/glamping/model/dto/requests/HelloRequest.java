package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Hello Request")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HelloRequest {
    @NotNull
    @Size(min = 2, max = 50)
    @Schema(
            description = "Message of the request",
            example = "Glamping",
            minimum = "2",
            maximum = "50"
    )
    private String message;
}
