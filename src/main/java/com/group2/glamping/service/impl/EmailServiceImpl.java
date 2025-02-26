package com.group2.glamping.service.impl;

import com.group2.glamping.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Email gửi đi
    private String fromEmail;

    @Override
    public void sendBookingConfirmation(String toEmail, String name, int bookingId, String campSiteName) {
        String subject = "[ASTRO GLAMPÉ] XÁC NHẬN ĐẶT CHỖ - BOOKING ID: #" + bookingId;
        String body = "<h3>Kính chào quý khách</h3>"
                + "<p>Chúng tôi xin thông báo rằng việc đặt chỗ của quý khách tại khu cắm trại : " + campSiteName + " đã được chấp nhận.</p>"
                + "<p>Hẹn gặp quý khách tại khu cắm trại " + campSiteName + "  !</p>"
                + "<br><p>Trân trọng,<br>Đội ngũ Astro Glamping</p>";

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendDeniedBookingEmail(String toEmail, String userName, int bookingId, String campSiteName, String deniedReason) {
        String subject = "[ASTRO GLAMPÉ] HỦY ĐẶT CHỖ - BOOKING ID: #" + bookingId;
        String body = "<h3>Kính chào quý khách " + userName + ",</h3>"
                + "<p>Chúng tôi rất tiếc khi phải thông báo rằng yêu cầu đặt chỗ của quý khách tại <b>" + campSiteName + "</b> đã không thể được chấp nhận.</p>"
                + "<p><b>Lý do:</b> " + deniedReason + "</p>"
                + "<p>Chúng tôi chân thành xin lỗi vì sự bất tiện này. Nếu cần thêm thông tin hoặc hỗ trợ, quý khách vui lòng liên hệ với chúng tôi.</p>"
                + "<p>Cảm ơn quý khách đã quan tâm và sử dụng dịch vụ của <b>ASTRO GLAMPÉ</b>. Chúng tôi hy vọng sẽ có cơ hội phục vụ quý khách trong tương lai.</p>"
                + "<br><p>Trân trọng,<br><b>Đội ngũ Astro Glamping</b></p>";

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
