package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.entity.ChatMessage;

public interface ChatService {

    /**
     * Gửi tin nhắn công khai đến tất cả người dùng
     */
    ChatMessage sendPublicMessage(ChatMessage chatMessage);
    /**
     * Gửi tin nhắn riêng tư giữa hai người dùng
     */
    void sendPrivateMessage(ChatMessage chatMessage);

}
