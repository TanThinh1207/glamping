package com.group2.glamping.model.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VerifyTokenRequest(
        @NotNull String idToken,
        String fcmToken,
        String deviceid) {
}
