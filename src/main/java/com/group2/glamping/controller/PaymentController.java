package com.group2.glamping.controller;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.PaymentRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.StripeResponse;
import com.group2.glamping.model.enums.PaymentStatus;
import com.group2.glamping.service.impl.StripeService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentController {

    private final StripeService stripeService;

    @Value("${stripe.url.redirect}")
    private String redirectUrl;

    @Value("${stripe.url.connect.account}")
    private String connectUrl;

    @Operation(
            summary = "Process a Stripe payment",
            description = "Initiate a payment using Stripe's API. This endpoint processes a payment request and returns a Stripe response.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid payment request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/checkout")
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
            @Parameter(description = "ID of the booking to refund", example = "1")
            @RequestParam Integer bookingId,

            @Parameter(description = "Deny reason", example = "Vi m ngu nen khong cho thue")
            @RequestParam String message
    ) {
        try {
            stripeService.refundPayment(bookingId, message);
            return ResponseEntity.ok(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Refund successful")
                    .data("Refund successful")
                    .build());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Refund failed: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Handle successful payment",
            description = "This endpoint is called when a payment is successfully completed. It updates the payment status to 'Completed' in the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid session ID or bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error or Stripe API failure")
            }
    )
    @GetMapping("/success")
    public void handleSuccess(
            @Parameter(description = "Stripe session ID", example = "cs_test_xxxxxxxxxxxxxxxxxxxxxxxx")
            @RequestParam("session_id") String sessionId,
            HttpServletResponse response) {
        try {
            stripeService.updatePaymentStatus(sessionId, PaymentStatus.Completed);

            log.info("Payment successful for session ID: {}", sessionId);

            response.sendRedirect(redirectUrl);
        } catch (StripeException | IOException e) {
            log.error("Failed to update payment status for session ID: {}", sessionId, e);
            throw new AppException(ErrorCode.PAYMENT_FAILED, "Failed to update payment status: " + e.getMessage());
        }
    }


    @Operation(
            summary = "Handle cancelled payment",
            description = "This endpoint is called when a payment is cancelled. It updates the payment status to 'Failed' in the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid session ID or bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error or Stripe API failure")
            }
    )
    @GetMapping("/cancel")
    public void handleCancel(
            @Parameter(description = "Stripe session ID", example = "cs_test_xxxxxxxxxxxxxxxxxxxxxxxx")
            @RequestParam("session_id") String sessionId,
            HttpServletResponse response) {
        try {
            stripeService.cancelPayment(sessionId);
            log.info("Payment cancelled for session ID: {}", sessionId);

            response.sendRedirect(redirectUrl);
        } catch (StripeException | IOException e) {
            log.error("Failed to update payment status for session ID: {}", sessionId, e);
            throw new AppException(ErrorCode.PAYMENT_FAILED, "Failed to update payment status: " + e.getMessage());
        }
    }

    @GetMapping("/connected-account")
    public RedirectView connectedAccount(@RequestParam Integer hostId) throws StripeException {
        String url = stripeService.createAccountLink(hostId);
        return new RedirectView(url);
    }

    @GetMapping("/connected-success")
    public RedirectView connectedSuccess() {
        return new RedirectView(connectUrl);
    }

}
