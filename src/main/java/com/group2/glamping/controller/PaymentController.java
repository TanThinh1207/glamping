package com.group2.glamping.controller;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.PaymentRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.StripeResponse;
import com.group2.glamping.model.enums.PaymentStatus;
import com.group2.glamping.service.impl.StripeService;
import com.group2.glamping.service.interfaces.BookingService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentController {


    private final BookingService bookingService;
    private final StripeService stripeService;


    @Operation(
            summary = "Process a Stripe payment",
            description = "Initiate a payment using Stripe's API. This endpoint processes a payment request and returns a Stripe response.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid payment request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/stripe/checkout")
    public ResponseEntity<StripeResponse> payStripe(
            @Parameter(
                    description = "Payment request containing payment details",
                    required = true
            )
            @RequestBody PaymentRequest stripeRequest) throws AppException {
        StripeResponse stripeResponse = stripeService.pay(stripeRequest);
        return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Transfer funds to a host",
            description = "Transfer a specified amount to a host's Stripe account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transfer successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/transfer")
    public ResponseEntity<BaseResponse> transferToHost(
            @Parameter(description = "Stripe account ID of the host", example = "acct_1QzvMNPRMkwRnnIv")
            @RequestParam String hostStripeAccountId,

            @Parameter(description = "Amount to transfer (in cents)", example = "1000")
            @RequestParam long amount
    ) {
        try {

            stripeService.transferToHost(hostStripeAccountId, amount);
            return ResponseEntity.ok(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Transfer successful")
                    .build());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Transfer failed: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Refund a payment to a customer",
            description = "Refund a specified amount to a customer for a given charge",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Refund successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/refund")
    public ResponseEntity<BaseResponse> refundPayment(
            @Parameter(description = "Charge ID of the payment to refund", example = "ch_1QzvMNPRMkwRnnIv")
            @RequestParam String chargeId,

            @Parameter(description = "Amount to refund (in cents)", example = "500")
            @RequestParam double amount
    ) {
        try {
            stripeService.refundPayment(chargeId, amount);
            return ResponseEntity.ok(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Refund successful")
                    .build());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Refund failed: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public ResponseEntity<BaseResponse> handleSuccess(
            @Parameter(description = "Stripe session ID", example = "cs_test_xxxxxxxxxxxxxxxxxxxxxxxx")
            @RequestParam("session_id") String sessionId) throws StripeException {
        try {
            // Update payment status to "Completed"
            stripeService.updatePaymentStatus(sessionId, PaymentStatus.Completed);
            log.info("Payment successful for session ID: {}", sessionId);

            // Return a success response
            return ResponseEntity.ok(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Payment successful!")
                    .data(sessionId) // Optionally include the session ID in the response
                    .build());
        } catch (StripeException e) {
            log.error("Failed to update payment status for session ID: {}", sessionId, e);
            throw new AppException(ErrorCode.PAYMENT_FAILED, "Failed to update payment status: " + e.getMessage());
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<BaseResponse> handleCancel(
            @Parameter(description = "Stripe session ID", example = "cs_test_xxxxxxxxxxxxxxxxxxxxxxxx")
            @RequestParam("session_id") String sessionId) throws StripeException {
        try {
            // Update payment status to "Failed"
            stripeService.updatePaymentStatus(sessionId, PaymentStatus.Failed);
            log.info("Payment cancelled for session ID: {}", sessionId);

            // Return a success response
            return ResponseEntity.ok(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Payment cancelled!")
                    .data(sessionId) // Optionally include the session ID in the response
                    .build());
        } catch (StripeException e) {
            log.error("Failed to update payment status for session ID: {}", sessionId, e);
            throw new AppException(ErrorCode.PAYMENT_FAILED, "Failed to update payment status: " + e.getMessage());
        }
    }


}
