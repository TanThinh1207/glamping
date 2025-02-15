package com.group2.glamping.service.impl;

import com.group2.glamping.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Email gửi đi
    private String fromEmail;

    @Override
    public void sendBookingConfirmation(String toEmail, String name, int bookingId, String campSiteName) {
        String subject = "[ASTRO GLAMPÉ] XÁC NHẬN ĐẶT CHỔ - BOOKING ID: #" + bookingId;
        String body = "<h3>Kính chào quý khách</h3>"
                + "<p>Chúng tôi xin thông báo rằng việc đặt chỗ của quý khách tại khu cắm trại : "+ campSiteName +" đã được chấp nhận.</p>"
                + "<p>Hẹn gặp quý khách tại khu cắm trại "+ campSiteName+"  !</p>"
                + "<br><p>Trân trọng,<br>Đội ngũ Astro Glamping</p>";

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true để gửi email dưới dạng HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
