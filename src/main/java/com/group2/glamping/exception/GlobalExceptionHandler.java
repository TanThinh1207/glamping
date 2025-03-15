package com.group2.glamping.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.FirebaseException;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse> handleAppException(AppException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getStatusCode().value())
                .body(BaseResponse.builder()
                        .statusCode(ex.getErrorCode().getCode())
                        .message(ex.getErrorCode().getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.builder()
                        .statusCode(9999)
                        .message("Internal Server Error: " + ex.getMessage() + ex)
                        .data(null)
                        .build());
    }

    @ExceptionHandler(FirebaseException.class)
    public ResponseEntity<BaseResponse> handleFirebaseException(FirebaseException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().ordinal())
                .body(BaseResponse.builder()
                        .statusCode(ex.getErrorCode().ordinal())
                        .message(ex.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<BaseResponse> handleStripeException(StripeException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(BaseResponse.builder()
                        .statusCode(ex.getStatusCode())
                        .message(ex.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<BaseResponse> handleJsonProcessingException(JsonProcessingException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .data(null)
                        .build());
    }
}
