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
        List<FcmToken> tokens = fcmTokenRepository.findByUserId(userId);
        List<String> fcmTokens = tokens.stream().map(FcmToken::getToken).toList();

        for (String token : fcmTokens) {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
//                            .setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT-YNnXzLl3_zcvyMQikTCis-2R3x_aPX-8CA&s")
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

