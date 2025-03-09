package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.PaymentRequest;
import com.group2.glamping.model.dto.response.StripeResponse;
import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.model.enums.BookingStatus;
import com.group2.glamping.model.enums.PaymentStatus;
import com.group2.glamping.repository.BookingRepository;
import com.group2.glamping.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Refund;
import com.stripe.model.Transfer;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.TransferCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.group2.glamping.utils.CurrencyConverter.convertVndToUsd;
import static com.stripe.Stripe.apiKey;

@Service
@RequiredArgsConstructor
public class StripeService {
    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${exchange.api.key}")
    private String exchangeApiKey;


    @PostConstruct
    public void init() {
        apiKey = secretKey; // Initialize Stripe with your API key
    }

    public final PaymentRepository paymentRepository;
    public final BookingRepository bookingRepository;

    public StripeResponse pay(PaymentRequest paymentRequest) {

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(paymentRequest.getName())
                .build();
        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(paymentRequest.getCurrency() == null ? "USD" : paymentRequest.getCurrency())
                .setUnitAmount((long) paymentRequest.getAmount())
                .setProductData(productData)
                .build();
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/api/stripe/success")
                .setCancelUrl("http://localhost:8080/api/stripe/cancel")
                .addLineItem(lineItem)
                .putMetadata("bookingId", String.valueOf(paymentRequest.getBookingId()))
                .build();

        Session session = null;
        try {
            session = Session.create(params);
            paymentRepository.save(Payment.builder()
                    .booking(bookingRepository.getReferenceById(paymentRequest.getBookingId()))
                    .status(PaymentStatus.Pending)
                    .totalAmount(paymentRequest.getAmount())
                    .paymentMethod("Stripe")
                    .build());
        } catch (StripeException e) {
            System.out.println(e.getMessage());
        }
        assert session != null;
        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created successfully")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    // Create Connected Account for Host
    public void createHostAccount(String email) throws StripeException {
        AccountCreateParams params =
                AccountCreateParams.builder()
                        .setType(AccountCreateParams.Type.EXPRESS)
                        .setCountry("US")
                        .setEmail(email)
                        .setCapabilities(
                                AccountCreateParams.Capabilities.builder()
                                        .setTransfers(
                                                AccountCreateParams.Capabilities.Transfers.builder()
                                                        .setRequested(true)
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        Account.create(params);
    }

    // Transfer payout to Host
    public void transferToHost(String hostStripeAccountId, long amountVnd) throws StripeException, IOException {
        try {
            long hostAmountVnd = (long) (amountVnd * 0.9);
            System.out.println("Amount after 10% fee (VND): " + hostAmountVnd);

            long amountUsdCents = (long) convertVndToUsd(hostAmountVnd, exchangeApiKey);
            System.out.println("Converted amount (USD cents): " + amountUsdCents);

            TransferCreateParams params =
                    TransferCreateParams.builder()
                            .setAmount(amountUsdCents) // Amount in USD cents
                            .setCurrency("usd") // Currency is USD
                            .setDestination(hostStripeAccountId) // Host's Stripe account ID
                            .setDescription("Payout for completed booking") // Description of the transfer
                            .build();

            Transfer transfer = Transfer.create(params);
            System.out.println("Transfer successful: " + transfer.getId());
        } catch (StripeException e) {
            System.err.println("Stripe transfer failed: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            System.err.println("Currency conversion failed: " + e.getMessage());
            throw e;
        }
    }

    // Refund payment to Customer
    public void refundPayment(String chargeId, double amount) throws StripeException {
        RefundCreateParams params =
                RefundCreateParams.builder()
                        .setCharge(chargeId)
                        .setAmount((long) amount)
                        .build();
        Payment payment = paymentRepository.findByTransactionId(chargeId).orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.Refund);
        bookingRepository.save(booking);
        Refund refund = Refund.create(params);
        System.out.println("Refund successful: " + refund.getId());
    }

    public void updatePaymentStatus(String sessionId, PaymentStatus paymentStatus) throws StripeException {
        Session session = getSessionDetails(sessionId);

        int bookingId = Integer.parseInt(session.getMetadata().get("bookingId"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseGet(() -> Payment.builder()
                        .sessionId(sessionId)
                        .booking(booking)
                        .paymentMethod("Stripe")
                        .totalAmount(session.getAmountTotal() / 100.0)
                        .build());

        payment.setStatus(paymentStatus);

        paymentRepository.save(payment);

        if (paymentStatus == PaymentStatus.Completed) {
            booking.setStatus(BookingStatus.Deposit);
        } else if (paymentStatus == PaymentStatus.Failed) {
            booking.setStatus(BookingStatus.Cancelled);
        }
        bookingRepository.save(booking);
    }

    public Session getSessionDetails(String sessionId) throws StripeException {
        SessionRetrieveParams params = SessionRetrieveParams.builder().build();
        return Session.retrieve(sessionId, params, null);
    }
}
