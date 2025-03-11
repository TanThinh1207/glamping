package com.group2.glamping.model.dto.requests;

import lombok.Builder;

@Builder
public record FcmTokenDeleteRequest(
        int userId,
        String deviceId
) {
}
