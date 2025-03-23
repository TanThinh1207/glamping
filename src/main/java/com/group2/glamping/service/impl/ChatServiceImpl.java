package com.group2.glamping.service.impl;

import com.group2.glamping.model.entity.ChatMessage;
import com.group2.glamping.service.interfaces.ChatRedisService;
import com.group2.glamping.service.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatRedisService chatRedisService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PushNotificationService pushNotificationService;
    @Override
    public ChatMessage sendPublicMessage(ChatMessage chatMessage) {
        logger.info("Public message from {}: {}", chatMessage.getSenderId(), chatMessage.getContent());
        return chatMessage;
    }

    @Override
    public void sendPrivateMessage(ChatMessage chatMessage) {
        if (chatMessage.getSenderId() == null || chatMessage.getRecipientId() == null) {
            chatMessage.setRecipientId(0);
        }

        logger.info("Private message from {} to {}: {}", chatMessage.getSenderId(), chatMessage.getRecipientId(), chatMessage.getContent());

        // Gửi tin nhắn qua WebSocket
        messagingTemplate.convertAndSend("/topic/private." + chatMessage.getRecipientId(), chatMessage);

        // Lưu tin nhắn vào Redis
        chatRedisService.saveChatMessage(chatMessage);

        // Push thông báo đẩy
        String title = "New Message from " + chatMessage.getSenderId();
        String body = chatMessage.getContent();

        pushNotificationService.sendNotification(chatMessage.getRecipientId(), title, body);
    }
}
