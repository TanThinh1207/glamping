package com.group2.glamping.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Request body for updating user information")
public record UserUpdateRequest(

        @NotNull(message = "First name is required")
        @Schema(description = "First name of the user", example = "Cristiano")
        String firstName,

        @NotNull(message = "Last name is required")
        @Schema(description = "Last name of the user", example = "Ronaldo")
        String lastName,

        @NotNull(message = "Phone number is required")
        @Size(min = 10, max = 15, message = "Phone number should be between 10 to 15 characters")
        @Schema(description = "Phone number of the user", example = "1234567890")
        String phone,

        @NotNull(message = "Address is required")
        @Schema(description = "Address of the user", example = "123 Manchester")
        String address,

        @NotNull(message = "Date of birth is required")
        @Schema(description = "Date of birth of the user", example = "2004-08-08")
        LocalDate dob,

//        @NotNull(message = "Status is required")
        @Schema(description = "Account status of the user", example = "true")
        Boolean status
) {
}
