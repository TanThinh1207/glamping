package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.PaymentRequest;
import com.group2.glamping.model.dto.response.StripeResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Refund;
import com.stripe.model.Transfer;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.TransferCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.group2.glamping.utils.CurrencyConverter.convertVndToUsd;
import static com.stripe.Stripe.apiKey;

@Service
public class StripeService {
    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${exchange.api.key}")
    private String exchangeApiKey;


    @PostConstruct
    public void init() {
        apiKey = secretKey; // Initialize Stripe with your API key
    }

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
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .addLineItem(lineItem)
                .putMetadata("bookingId", String.valueOf(paymentRequest.getBookingId()))
                .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            //log
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
            // Step 1: Deduct 10% fee from the amount (in VND)
            long hostAmountVnd = (long) (amountVnd * 0.9);
            System.out.println("Amount after 10% fee (VND): " + hostAmountVnd);

            // Step 2: Convert VND to USD (in cents)
            long amountUsdCents = (long) convertVndToUsd(hostAmountVnd, exchangeApiKey);
            System.out.println("Converted amount (USD cents): " + amountUsdCents);

            // Step 3: Create transfer parameters
            TransferCreateParams params =
                    TransferCreateParams.builder()
                            .setAmount(amountUsdCents) // Amount in USD cents
                            .setCurrency("usd") // Currency is USD
                            .setDestination(hostStripeAccountId) // Host's Stripe account ID
                            .setDescription("Payout for completed booking") // Description of the transfer
                            .build();

            // Step 4: Execute the transfer
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

        Refund refund = Refund.create(params);
        System.out.println("Refund successful: " + refund.getId());
    }

}
