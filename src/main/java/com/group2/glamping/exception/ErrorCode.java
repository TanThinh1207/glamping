package com.group2.glamping.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    WRONG_PASSWORD(1008, "Wrong password", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1009, "Your age must be at least 16", HttpStatus.BAD_REQUEST),
    CAMP_SITE_NOT_FOUND(10010, "Camp site not found", HttpStatus.NOT_FOUND),
    INACTIVE_USER(10011, "User not activated", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(10012, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    SELECTION_NOT_FOUND(10011, "Selection not found", HttpStatus.NOT_FOUND),
    UTILITY_NOT_FOUND(10012, "Utility not found", HttpStatus.NOT_FOUND),
    CAMP_TYPE_NOT_FOUND(10013, "Camp type not found", HttpStatus.NOT_FOUND),
    BOOKING_NOT_FOUND(10014, "Booking not found", HttpStatus.NOT_FOUND),

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
