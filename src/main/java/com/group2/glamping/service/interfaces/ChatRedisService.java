package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.entity.ChatMessage;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public interface ChatRedisService {

    public void saveChatMessage(ChatMessage chatMessage);

    public List<ChatMessage> getChatHistory(String sender, String recipient);

    void clearChatHistory(String sender, String recipient);

}
