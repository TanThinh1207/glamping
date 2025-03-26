package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.PaymentRequest;
import com.group2.glamping.model.dto.response.StripeResponse;
import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.model.enums.BookingStatus;
import com.group2.glamping.model.enums.PaymentStatus;
import com.group2.glamping.repository.BookingRepository;
import com.group2.glamping.repository.PaymentRepository;
import com.group2.glamping.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.TransferCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.group2.glamping.utils.CurrencyConverter.convertVndToUsd;
import static com.stripe.Stripe.apiKey;

@Service
@RequiredArgsConstructor
public class StripeService {
    private final PushNotificationService pushNotificationService;
    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${exchange.api.key}")
    private String exchangeApiKey;

    @Value("${stripe.callback.url}")
    private String stripeCallback;

    @PostConstruct
    public void init() {
        apiKey = secretKey;
    }

    public final PaymentRepository paymentRepository;
    public final BookingRepository bookingRepository;
    public final UserRepository userRepository;

    public StripeResponse pay(PaymentRequest paymentRequest) {

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(paymentRequest.name())
                .build();
        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(paymentRequest.currency() == null ? "USD" : paymentRequest.currency())
                .setUnitAmount((long) paymentRequest.amount())
                .setProductData(productData)
                .build();
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeCallback + "api/payments/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(stripeCallback + "api/payments/cancel?session_id={CHECKOUT_SESSION_ID}")
                .addLineItem(lineItem)
                .putMetadata("bookingId", String.valueOf(paymentRequest.bookingId()))
                .build();

        Session session = null;
        try {
            System.out.println("Creating Stripe session...");
            session = Session.create(params);
            System.out.println("Stripe session created: " + session.getId());
            paymentRepository.save(Payment.builder()
                    .booking(bookingRepository.getReferenceById(paymentRequest.bookingId()))
                    .status(PaymentStatus.Pending)
                    .totalAmount(paymentRequest.amount())
                    .paymentMethod("Stripe")
                    .sessionId(session.getId())
                    .url(session.getUrl())
                    .completedTime(LocalDateTime.now())
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

        Account account = Account.create(params);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setConnectionId(account.getId());
        userRepository.save(user);
    }

    // Transfer payout to Host
    public void transferToHost(int hostID, long amountVnd) throws StripeException, IOException {
        try {
            System.out.println("Amount after 10% fee (VND): " + amountVnd);
            double amountUsd = convertVndToUsd(amountVnd, exchangeApiKey);
            long amountUsdCents = Math.round(amountUsd * 100);
            System.out.println("Converted amount (USD cents): " + amountUsdCents);

            User user = userRepository.findById(hostID).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));


            TransferCreateParams params =
                    TransferCreateParams.builder()
                            .setAmount(amountUsdCents)
                            .setCurrency("usd")
                            .setDestination(user.getConnectionId())
                            .setDescription("Payout for completed booking")
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
    public void refundPayment(int bookingId, String message) throws StripeException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        Payment payment = booking.getPaymentList().getFirst();
        String chargeId = payment.getTransactionId();
        double amount = payment.getTotalAmount();
        RefundCreateParams params =
                RefundCreateParams.builder()
                        .setCharge(chargeId)
                        .setAmount((long) amount)
                        .build();
        booking.setStatus(BookingStatus.Refund);
        booking.setMessage(message);
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

        if (session.getPaymentIntent() != null) {
            String paymentIntentId = session.getPaymentIntent();
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            payment.setTransactionId(paymentIntent.getLatestCharge());
        }
        paymentRepository.save(payment);

        if (paymentStatus == PaymentStatus.Completed) {
            pushNotificationService.sendNotification(booking.getCampSite().getUser().getId(), "New Booking For " + booking.getCampSite().getName(),
                    "A new booking has been made for your campsite " + booking.getCampSite().getName() + "from " + booking.getUser().getFirstname());
            booking.setStatus(BookingStatus.Deposit);
        } else if (paymentStatus == PaymentStatus.Failed) {
            booking.setStatus(BookingStatus.Cancelled);
        }
        bookingRepository.save(booking);
    }

    public Integer cancelPayment(String sessionId) throws StripeException {
        Session session = getSessionDetails(sessionId);

        if (!"expired".equals(session.getStatus())) {
            session.expire();
            System.out.println("Session expired: " + session.getStatus());
        }

        int bookingId = Integer.parseInt(session.getMetadata().get("bookingId"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseGet(() -> Payment.builder()
                        .sessionId(sessionId)
                        .booking(booking)
                        .paymentMethod("Stripe")
                        .totalAmount(session.getAmountTotal() / 100.0)
                        .build());

        payment.setStatus(PaymentStatus.Failed);
        booking.setStatus(BookingStatus.Cancelled);
        bookingRepository.save(booking);
        return bookingId;
    }

    @Scheduled(fixedRate = 86400000)
    public void cancelExpiredPayments() {
        LocalDateTime timeLimit = LocalDateTime.now().minusHours(24);
        List<Payment> expiredPayments = paymentRepository.findPaymentsOlderThan24Hours(timeLimit, PaymentStatus.Pending);

        for (Payment payment : expiredPayments) {
            try {
                Session session = getSessionDetails(payment.getSessionId());
                if (session.getStatus().equals("expired")) {
                    Booking booking = payment.getBooking();
                    booking.setStatus(BookingStatus.Cancelled);
                    bookingRepository.save(booking);

                    payment.setStatus(PaymentStatus.Failed);
                    paymentRepository.save(payment);

                    System.out.println("Payment and booking updated as expired for session: " + session.getId());
                }
            } catch (StripeException e) {
                System.err.println("Error checking session status: " + e.getMessage());
            }
        }
    }

    public Session getSessionDetails(String sessionId) throws StripeException {
        SessionRetrieveParams params = SessionRetrieveParams.builder().build();
        return Session.retrieve(sessionId, params, null);
    }

    public String createAccountLink(int hostId) throws StripeException {
        User user = userRepository.findById(hostId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        AccountLinkCreateParams params =
                AccountLinkCreateParams.builder()
                        .setAccount(user.getConnectionId())
                        .setRefreshUrl(stripeCallback + "account?status=fail")
                        .setReturnUrl(stripeCallback + "account?status=success")
                        .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                        .build();
        AccountLink accountLink = AccountLink.create(params);
        return accountLink.getUrl();
    }

    public boolean isAccountRestricted(String accountId) throws StripeException {
        Account account = Account.retrieve(accountId);
        return !account.getRequirements().getCurrentlyDue().isEmpty();
    }
}
