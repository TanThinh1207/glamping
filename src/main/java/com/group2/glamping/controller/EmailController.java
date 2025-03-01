package com.group2.glamping.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class EmailController {

    private final JavaMailSender mailSender;

    @GetMapping("/auto-email")
    public String sendEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("nguyentuankhang802@gmail.com");

            message.setTo("nguyentuankhang802@gmail.com");
            message.setSubject("Simple test email from Kouta");
            message.setText("This a sample of email body");

            mailSender.send(message);
            return "success";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @GetMapping("/send-email-with-attachment")
//    public String sendEmailWithAttachment() {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setFrom("nguyentuankhang802@gmail.com");
//            helper.setTo("nguyentuankhang802@gmail.com");
//
//            helper.setSubject("Simple email with attachment from Kouta");
//
//            helper.setText("Here are the attachments: ");
//
//            helper.addAttachment("avt.jpg", new File("C:\\Users\\THIS PC\\Pictures\\avt.jpg"));
//            helper.addAttachment("Kanji1.pptx", new File("D:\\LearningMaterial\\JPD316\\Kanji\\Kanji1.pptx"));
//
//            mailSender.send(message);
//            return "success";
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
}

