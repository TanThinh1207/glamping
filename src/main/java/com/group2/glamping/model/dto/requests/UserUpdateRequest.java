package com.group2.glamping.model.dto.requests;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UserUpdateRequest(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String phone,
        @NotNull String address,
        @NotNull LocalDateTime dob,
        @NotNull Boolean status
) {
}
