package com.group2.glamping.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    WRONG_PASSWORD(1008, "Wrong password", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1009, "Your age must be at least 16", HttpStatus.BAD_REQUEST),
    CAMP_SITE_NOT_FOUND(1010, "Camp site not found", HttpStatus.NOT_FOUND),
    INACTIVE_USER(1011, "User not activated", HttpStatus.BAD_REQUEST),
    SELECTION_NOT_FOUND(1012, "Selection not found", HttpStatus.NOT_FOUND),
    UTILITY_NOT_FOUND(1013, "Utility not found", HttpStatus.NOT_FOUND),
    CAMP_TYPE_NOT_FOUND(1014, "Camp type not found", HttpStatus.NOT_FOUND),
    BOOKING_NOT_FOUND(1015, "Booking not found", HttpStatus.NOT_FOUND),
    FILE_NOT_FOUND(10015, "File not found", HttpStatus.NOT_FOUND),
    S3_ERROR(10016, "S3 error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_USERNAME(10012, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PLACE_TYPE_NOT_FOUND(10016, "Place type not found", HttpStatus.NOT_FOUND),
    USER_NOT_AVAILABLE(10017, "User is currently disable", HttpStatus.NOT_FOUND),
    INVALID_REQUEST(10018, "Invalid request", HttpStatus.BAD_REQUEST),
    FACILITY_NOT_FOUND(10019, "Facility not found", HttpStatus.NOT_FOUND),
    BOOKING_DETAIL_NOT_FOUND(10020, "Booking detail not found", HttpStatus.NOT_FOUND),
    CAMP_NOT_FOUND(10021, "Camp not found", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_FOUND(10022, "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED(10023, "Payment failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_DATE_FORMAT(10024, "Invalid date format", HttpStatus.BAD_REQUEST),
    NO_AVAILABLE_CAMP(10025, "No available camp", HttpStatus.NOT_FOUND),
    FACILITY_LIST_CANNOT_BE_EMPTY(10026, "Facility list cannot be empty", HttpStatus.BAD_REQUEST),
    PLACE_TYPE_LIST_CANNOT_BE_EMPTY(10027, "Place type list cannot be empty", HttpStatus.BAD_REQUEST),
    UTILITY_LIST_CANNOT_BE_EMPTY(10028, "Utility list cannot be empty", HttpStatus.BAD_REQUEST),
    REPORT_NOT_FOUND(10029, "Report not found", HttpStatus.NOT_FOUND),
    ID_REQUIRED_FOR_UPDATE(10030, "ID is required for update operation", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }


}
