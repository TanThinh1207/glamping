package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.PaymentRequest;
import com.group2.glamping.model.dto.response.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
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

}
