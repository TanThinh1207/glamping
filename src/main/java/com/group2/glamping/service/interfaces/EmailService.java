package com.group2.glamping.service.interfaces;

public interface EmailService {

    void sendBookingConfirmation(String toEmail, String userName, int bookingId, String campSiteName);

    void sendDeniedBookingEmail(String toEmail, String userName, int bookingId, String campSiteName, String deniedReason);

    void sendEmail(String toEmail, String subject, String body);
}
