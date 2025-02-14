package com.group2.glamping.model.dto.requests;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Date;

public record UserUpdateRequest(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String phone,
        @NotNull String address,
        @NotNull Date dob,
        @NotNull Boolean status
) {
}
