package com.group2.glamping.model.dto.requests;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserUpdateRequest(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String phone,
        @NotNull String address,
        @NotNull LocalDate dob,
        @NotNull Boolean status
) {
}
