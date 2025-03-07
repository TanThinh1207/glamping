package com.group2.glamping.controller;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.PaymentRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.StripeResponse;
import com.group2.glamping.service.impl.StripeService;
import com.group2.glamping.service.interfaces.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
