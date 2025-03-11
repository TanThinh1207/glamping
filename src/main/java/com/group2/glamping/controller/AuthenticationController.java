package com.group2.glamping.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.model.dto.requests.VerifyTokenRequest;
import com.group2.glamping.model.dto.response.AuthenticationResponse;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.impl.AuthenticationService;
import com.stripe.exception.StripeException;
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

    private final FirebaseAuth auth;

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse> verifyToken(@RequestBody VerifyTokenRequest tokenRequest) {
        try {
            FirebaseToken decodedToken = auth.verifyIdToken(tokenRequest.idToken());
            AuthenticationResponse resp = authenticationService.verify(tokenRequest, decodedToken.getEmail());
            return ResponseEntity.ok(BaseResponse.builder()
                    .message("Login successful")
                    .data(resp)
                    .statusCode(HttpStatus.OK.value())
                    .build());
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(BaseResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(e.getErrorCode())
                    .build());
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .message("Firebase Authentication failed: " + e.getMessage())
                            .build());
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .message("Stripe exception failed: " + e.getMessage())
                            .build());
        }
    }

    //    @PostMapping("/register")
//    @Operation(
//            summary = "Register a new user",
//            description = "Registers a new user and returns authentication details",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Registration successful"),
//                    @ApiResponse(responseCode = "400", description = "Registration failed due to invalid data")
//            }
//    )
//    public ResponseEntity<BaseResponse> register(@RequestBody RegisterRequest request) {
//        try {
//
//            return ResponseEntity.ok(BaseResponse.builder()
//                    .data(authenticationService.register(request))
//                    .message("Register successfully")
//                    .statusCode(HttpStatus.OK.value())
//                    .build());
//        } catch (AppException e) {
//            return ResponseEntity.badRequest().body(BaseResponse.builder()
//                    .statusCode(e.getErrorCode().getCode())
//                    .message(e.getMessage())
//                    .build());
//        }
//    }
//
//    @PostMapping("/authenticate")
//    @Operation(
//            summary = "Authenticate user",
//            description = "Authenticates a user and returns a token",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Authentication successful"),
//                    @ApiResponse(responseCode = "400", description = "Authentication failed due to invalid credentials")
//            }
//    )
//    public ResponseEntity<BaseResponse> authenticate(@RequestBody AuthenticationRequest request) {
//        try {
//            return ResponseEntity.ok(BaseResponse.builder()
//                    .message("Authenticate successfully")
//                    .statusCode(HttpStatus.OK.value())
//                    .data(authenticationService.authenticate(request))
//                    .build());
//        } catch (AppException e) {
//            return ResponseEntity.badRequest().body(BaseResponse.builder()
//                    .statusCode(e.getErrorCode().getCode())
//                    .message(e.getMessage())
//                    .build());
//        }
//    }


}
