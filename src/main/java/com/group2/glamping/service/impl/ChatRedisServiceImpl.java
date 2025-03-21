package com.group2.glamping.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.glamping.model.entity.ChatMessage;
import com.group2.glamping.service.interfaces.ChatRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class ChatRedisServiceImpl implements ChatRedisService {

    private static final Logger logger = LoggerFactory.getLogger(ChatRedisServiceImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final String CHAT_PREFIX = "chat:";  // Key prefix

    private String getChatKey(String sender, String recipient) {
        List<String> participants = Arrays.asList(sender, recipient);
        Collections.sort(participants); // Sắp xếp để đảm bảo key nhất quán
        return CHAT_PREFIX + participants.get(0) + ":" + participants.get(1);
    }

    @Override
    public void saveChatMessage(ChatMessage chatMessage) {
        String key = getChatKey(chatMessage.getSender(), chatMessage.getRecipient());
        try {
            String jsonMessage = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.opsForList().rightPush(key, jsonMessage);
        } catch (JsonProcessingException e) {
            logger.error("Error saving chat message to Redis", e);
        }
    }

    @Override
    public List<ChatMessage> getChatHistory(String sender, String recipient) {
        String key = getChatKey(sender, recipient);
        List<Object> rawMessages = redisTemplate.opsForList().range(key, 0, -1);

        if (rawMessages == null || rawMessages.isEmpty()) {
            return Collections.emptyList();
        }

        return rawMessages.stream()
                .map(obj -> {
                    try {
                        if (obj instanceof String) {
                            return objectMapper.readValue((String) obj, ChatMessage.class);
                        } else {
                            return objectMapper.convertValue(obj, ChatMessage.class);
                        }
                    } catch (JsonProcessingException e) {
                        logger.error("Error parsing chat message from Redis: {}", obj, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public void clearChatHistory(String sender, String recipient) {
        if (sender == null || recipient == null || sender.isEmpty() || recipient.isEmpty()) {
            throw new IllegalArgumentException("Sender and recipient must not be null or empty");
        }
        String chatKey = getChatKey(sender, recipient);
        redisTemplate.delete(chatKey);
        logger.info("✅ Cleared chat history for {} and {}", sender, recipient);
    }


}

