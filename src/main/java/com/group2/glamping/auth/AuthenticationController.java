package com.group2.glamping.auth;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.model.dto.requests.AuthenticationRequest;
import com.group2.glamping.model.dto.requests.RegisterRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
            @RequestBody RegisterRequest request
    ) {
        try {
            return ResponseEntity.ok(BaseResponse.builder()
                    .data(authenticationService.register(request))
                    .message("Register successfully")
                    .statusCode(HttpStatus.OK.value())
                    .build());
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(BaseResponse.builder()
                    .statusCode(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build());
        }

    }


    @PostMapping("/authenticate")
    public ResponseEntity<BaseResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {

        try {
            return ResponseEntity.ok(BaseResponse.builder()
                    .message("Authenticate successfully")
                    .statusCode(HttpStatus.OK.value())
                    .data(authenticationService.authenticate(request))
                    .build());
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(BaseResponse.builder()
                    .statusCode(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build());
        }
    }


}
