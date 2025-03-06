package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.PaymentRequest;
import com.group2.glamping.model.dto.response.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Refund;
import com.stripe.model.Transfer;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.TransferCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    @Value("${stripe.secretKey}")
    private String secretKey;


    public StripeResponse pay(PaymentRequest paymentRequest) {
        Stripe.apiKey = secretKey;

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
    public Account createHostAccount(String email) throws StripeException {
        AccountCreateParams params =
                AccountCreateParams.builder()
                        .setType(AccountCreateParams.Type.EXPRESS)
                        .setCountry("US")
                        .setEmail(email)
                        .setCapabilities(
                                AccountCreateParams.Capabilities.builder()
                                        .setTransfers(AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
                                        .build()
                        )
                        .build();

        return Account.create(params);
    }

    // Transfer payout to Host
    public void transferToHost(String hostStripeAccountId, long amountInCents, long platformFeeInCents) throws StripeException {
        long hostAmount = amountInCents - platformFeeInCents;

        TransferCreateParams params =
                TransferCreateParams.builder()
                        .setAmount(hostAmount)
                        .setCurrency("VND")
                        .setDestination(hostStripeAccountId)
                        .setDescription("Payout for completed booking")
                        .build();

        Transfer transfer = Transfer.create(params);
        System.out.println("Transfer successful: " + transfer.getId());
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
