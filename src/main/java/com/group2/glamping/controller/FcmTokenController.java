package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.FcmTokenRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fcm-tokens")
@RequiredArgsConstructor
public class FcmTokenController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<BaseResponse> saveFcmToken(@RequestBody FcmTokenRequest fcmToken) {
        return ResponseEntity.ok(BaseResponse.builder()
                .message("Save fcmToken successfully")
                .data(userService.updateFcmToken(fcmToken.userId(), fcmToken.fcmToken()))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

}
