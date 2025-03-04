package com.group2.glamping.controller;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.model.dto.requests.PaymentRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.PaymentBaseResponse;
import com.group2.glamping.model.dto.response.StripeResponse;
import com.group2.glamping.model.enums.PaymentStatus;
import com.group2.glamping.service.impl.StripeService;
import com.group2.glamping.service.interfaces.BookingService;
import com.group2.glamping.service.interfaces.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final StripeService stripeService;


    @PostMapping("/stripe/checkout")
    public ResponseEntity<StripeResponse> payStripe(@RequestBody PaymentRequest stripeRequest) throws AppException {
        StripeResponse stripeResponse = stripeService.pay(stripeRequest);
        return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
    }


    @GetMapping()
    public ResponseEntity<BaseResponse> findAll() {
        List<PaymentBaseResponse> payments = paymentService.findAll();
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Find all payments successfully")
                        .data(payments)
                        .statusCode(HttpStatus.OK.value())
                        .build()

        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getPaidAmountByBookingId(@PathVariable int bookingId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .data(paymentService.getPaidAmountByBooking(bookingId))
                .statusCode(HttpStatus.OK.value())
                .message("Paid amount by booking id successfully")
        );
    }

    @GetMapping("/vn-pay")
    public ResponseEntity<BaseResponse> pay(HttpServletRequest request) {
        return new ResponseEntity<>(
                BaseResponse.builder()
                        .message("Success")
                        .data(paymentService.createVnPayPayment(request))
                        .statusCode(HttpStatus.OK.value())
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/refund")
    public ResponseEntity<BaseResponse> refund(@RequestParam Integer paymentId,
                                               @RequestParam BigDecimal refundAmount,
                                               @RequestParam String reason) {
        return new ResponseEntity<>(
                BaseResponse.builder()
                        .message("Success")
                        .data(paymentService.refundPayment(paymentId, refundAmount, reason))
                        .statusCode(HttpStatus.OK.value())
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<BaseResponse> payCallbackHandler(
            @RequestParam String vnp_ResponseCode,
            @RequestParam Integer bookingId) {
        if (vnp_ResponseCode.equals("00")) {
            // Payment successful
            bookingService.confirmPaymentSuccess(bookingId);
            return new ResponseEntity<>(
                    BaseResponse.builder()
                            .message("Payment successful")
                            .data(null)
                            .statusCode(HttpStatus.OK.value())
                            .build(),
                    HttpStatus.OK
            );
        } else {
            // Payment failed
            return new ResponseEntity<>(
                    BaseResponse.builder()
                            .message("Payment failed")
                            .data(null)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("/info/{bookingId}")
    public ResponseEntity<?> findPaymentByOrderId(@PathVariable Integer bookingId) {
        try {
            Optional<PaymentBaseResponse> paymentResponse = paymentService.findPaymentByBookingId(bookingId);
            return new ResponseEntity<>(
                    BaseResponse.builder()
                            .message("Payment details fetched successfully")
                            .data(paymentResponse.orElse(null))
                            .statusCode(HttpStatus.OK.value())
                            .build(),
                    HttpStatus.OK
            );
        } catch (AppException e) {
            return new ResponseEntity<>(
                    BaseResponse.builder()
                            .message(e.getMessage())
                            .data(null)
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/status")
    public ResponseEntity<BaseResponse> findPaymentByStatus(@RequestParam PaymentStatus status) {
        List<PaymentBaseResponse> paymentResponse = paymentService.findByStatus(status);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Find payments by status successfully")
                        .data(paymentResponse)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }
}
