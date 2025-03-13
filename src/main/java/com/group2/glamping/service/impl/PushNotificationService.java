package com.group2.glamping.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.group2.glamping.model.entity.FcmToken;
import com.group2.glamping.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final FcmTokenRepository fcmTokenRepository;


    public void sendNotification(int userId, String title, String body) {
        System.out.println(userId);
        List<FcmToken> tokens = fcmTokenRepository.findByUserId(userId);

        for (FcmToken token : tokens) {
            Message message = Message.builder()
                    .setToken(token.getToken())
                    .setNotification(Notification.builder()
                            .setImage("https://cdn.dribbble.com/userupload/36626798/file/original-83c59b604abf17b46f2dafd5dd0c7e4f.png?resize=400x0")
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
                    .build();
            try {
                FirebaseMessaging.getInstance().sendAsync(message).get();
            } catch (ExecutionException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

