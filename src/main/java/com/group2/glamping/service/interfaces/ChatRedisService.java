package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.response.ChatHistoryResponse;
import com.group2.glamping.model.dto.response.UserChatInfoResponse;
import com.group2.glamping.model.entity.ChatMessage;

import java.util.List;

public interface ChatRedisService {

    void saveChatMessage(ChatMessage chatMessage);

    ChatHistoryResponse getChatHistory(Integer senderId, Integer recipientId, int page, int size, String sortBy, String direction);

    void clearChatHistory(Integer senderId, Integer recipientId);

    List<String> getChatKeysByUserId(Integer userId);

    List<UserChatInfoResponse> getRecipientsByUserId(Integer userId);

    }
