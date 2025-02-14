package com.group2.glamping.auth;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.model.dto.requests.AuthenticationRequest;
import com.group2.glamping.model.dto.requests.RegisterRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication API", description = "APIs for user authentication and registration")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user and returns authentication details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registration successful"),
                    @ApiResponse(responseCode = "400", description = "Registration failed due to invalid data")
            }
    )
    public ResponseEntity<BaseResponse> register(@RequestBody RegisterRequest request) {
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
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user and returns a token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentication successful"),
                    @ApiResponse(responseCode = "400", description = "Authentication failed due to invalid credentials")
            }
    )
    public ResponseEntity<BaseResponse> authenticate(@RequestBody AuthenticationRequest request) {
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
