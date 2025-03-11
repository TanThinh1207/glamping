package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.FcmTokenDeleteRequest;
import com.group2.glamping.model.dto.requests.FcmTokenRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping
    @Transactional
    public ResponseEntity<BaseResponse> deleteFcmToken(
            @RequestParam("userId") Integer userId,
            @RequestParam("deviceId") String deviceId
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .message("Delete fcmToken successfully")
                .data(userService.removeFcmToken(userId, deviceId))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

}
