package com.group2.glamping.model.dto.requests;

public record FcmTokenRequest(
        int userId,
        String fcmToken
) {
}
