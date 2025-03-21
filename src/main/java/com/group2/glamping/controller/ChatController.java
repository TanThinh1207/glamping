package com.group2.glamping.controller;

import com.group2.glamping.model.entity.ChatMessage;
import com.group2.glamping.service.interfaces.ChatRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatRedisService chatRedisService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @MessageMapping("/host-chatting")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/sendToUser")
    public void sendToUser(@Payload ChatMessage chatMessage) {
        if (chatMessage.getRecipient() == null || chatMessage.getRecipient().trim().isEmpty()) {
            chatMessage.setRecipient("host");
        }

        // Log tin nhắn nhận được
        logger.info("Received private message from {} to {}: {}",
                chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getContent());

        // Gửi tin nhắn qua WebSocket
        messagingTemplate.convertAndSend("/topic/private." + chatMessage.getRecipient(), chatMessage);

        // Lưu tin nhắn vào Redis
        chatRedisService.saveChatMessage(chatMessage);

    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @RequestParam String sender,
            @RequestParam String recipient) {
        if (sender == null || recipient == null || sender.isEmpty() || recipient.isEmpty()) {
            throw new IllegalArgumentException("Sender and recipient must not be null or empty");
        }
        // Lấy lịch sử chat từ Redis dựa trên 2 key sender và recipient
        List<ChatMessage> history = chatRedisService.getChatHistory(sender, recipient);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearChatHistory(@RequestParam String sender, @RequestParam String recipient) {
        try {
            chatRedisService.clearChatHistory(sender, recipient);
            return ResponseEntity.ok("Chat history cleared from Redis.");
        } catch (Exception e) {
            logger.error("Failed to clear chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to clear chat history.");
        }
    }

}
