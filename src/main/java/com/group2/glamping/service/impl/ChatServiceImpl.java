package com.group2.glamping.service.impl;

import com.group2.glamping.model.entity.ChatMessage;
import com.group2.glamping.service.interfaces.ChatRedisService;
import com.group2.glamping.service.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatRedisService chatRedisService;
    private final SimpMessagingTemplate messagingTemplate;

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
        messagingTemplate.convertAndSend("/topic/private." + chatMessage.getRecipientId(), chatMessage);
        chatRedisService.saveChatMessage(chatMessage);
//        messagingTemplate.convertAndSend("/topic/notifications",
//                String.format("User %d sent a message to User %d", chatMessage.getSenderId(), chatMessage.getRecipientId()));
//
    }
}
