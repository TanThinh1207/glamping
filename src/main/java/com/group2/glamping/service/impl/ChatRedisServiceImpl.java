package com.group2.glamping.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.group2.glamping.model.dto.response.ChatHistoryResponse;
import com.group2.glamping.model.dto.response.ChatMessageResponse;
import com.group2.glamping.model.dto.response.UserChatInfoResponse;
import com.group2.glamping.model.entity.ChatMessage;
import com.group2.glamping.service.interfaces.ChatRedisService;
import com.group2.glamping.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRedisServiceImpl implements ChatRedisService {

    private static final Logger logger = LoggerFactory.getLogger(ChatRedisServiceImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final UserService userService;

    private static final String CHAT_PREFIX = "chat:";

    private String getChatKey(Integer senderId, Integer recipientId) {
        if (senderId == null || recipientId == null) {
            throw new IllegalArgumentException("SenderId and recipientId must not be null");
        }
        List<Integer> participants = Arrays.asList(senderId, recipientId);
        Collections.sort(participants);
        return CHAT_PREFIX + participants.get(0) + ":" + participants.get(1);
    }

    @Override
    public void saveChatMessage(ChatMessage chatMessage) {
        String key = getChatKey(chatMessage.getSenderId(), chatMessage.getRecipientId());
        try {
            String jsonMessage = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.opsForList().rightPush(key, jsonMessage);
        } catch (JsonProcessingException e) {
            logger.error("Error saving chat message to Redis", e);
        }
    }

    @Override
    public ChatHistoryResponse getChatHistory(Integer senderId, Integer recipientId, int page, int size, String sortBy, String direction) {
        String key = getChatKey(senderId, recipientId);
        List<Object> rawMessages = redisTemplate.opsForList().range(key, 0, -1);

        if (rawMessages == null || rawMessages.isEmpty()) {
            return new ChatHistoryResponse(Collections.emptyList(), 0, 0, page, size);
        }

        List<ChatMessage> allMessages = rawMessages.stream()
                .map(obj -> {
                    try {
                        return objectMapper.readValue(obj.toString(), ChatMessage.class);
                    } catch (JsonProcessingException e) {
                        logger.error("🚨 Error parsing chat message from Redis: {}", obj, e);
                        return null; // Tránh lỗi, nhưng có thể bỏ sót tin nhắn
                    }
                })
                .filter(Objects::nonNull) // 🔥 Lọc tin nhắn bị lỗi
                .collect(Collectors.toList());

        // Đảo ngược tin nhắn (mới nhất ở cuối)
        Collections.reverse(allMessages);

        // 🏷 Chọn cách sắp xếp
        Comparator<ChatMessage> comparator = Comparator.comparing(ChatMessage::getTimestamp);
        if ("senderId".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(ChatMessage::getSenderId);
        } else if ("timestamp".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(ChatMessage::getTimestamp);
        }
        if ("DESC".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        allMessages.sort(comparator);

        // Xử lý phân trang
        int start = page * size;
        int end = Math.min(start + size, allMessages.size());
        List<ChatMessage> pagedMessages = (start < allMessages.size()) ? allMessages.subList(start, end) : Collections.emptyList();

        // Chuyển đổi sang `ChatMessageResponse`
        List<ChatMessageResponse> responseMessages = pagedMessages.stream()
                .map(msg -> new ChatMessageResponse(
                        msg.getSenderId(),
                        msg.getRecipientId(),
                        msg.getTimestamp(),
                        msg.getContent()
                ))
                .collect(Collectors.toList());

        // Trả về response đầy đủ
        return new ChatHistoryResponse(
                responseMessages,
                allMessages.size(),
                (int) Math.ceil((double) allMessages.size() / size),
                page,
                size
        );
    }


    @Override
    public void clearChatHistory(Integer senderId, Integer recipientId) {
        String chatKey = getChatKey(senderId, recipientId);
        redisTemplate.delete(chatKey);
        logger.info("Cleared chat history for senderId {} and recipientId {}", senderId, recipientId);
    }

    @Override
    public List<String> getChatKeysByUserId(Integer userId) {
        List<String> chatKeys = new ArrayList<>();
        String pattern = CHAT_PREFIX + "*"; // Tìm tất cả khóa bắt đầu với "chat:"

        try (Cursor<byte[]> cursor = Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .keyCommands()
                .scan(KeyScanOptions.scanOptions().match(pattern).count(1000).build())) {

            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                if (key.contains(":" + userId) || key.startsWith(CHAT_PREFIX + userId + ":")) {
                    chatKeys.add(key);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching chat keys for user {}", userId, e);
        }


        return chatKeys;
    }


    @Override
    public List<UserChatInfoResponse> getRecipientsByUserId(Integer userId) {
        List<String> chatKeys = getChatKeysByUserId(userId);
        Set<Integer> recipientIds = new HashSet<>();

        for (String key : chatKeys) {
            String[] parts = key.replace(CHAT_PREFIX, "").split(":");
            if (parts.length == 2) {
                Integer user1 = Integer.parseInt(parts[0]);
                Integer user2 = Integer.parseInt(parts[1]);

                if (user1.equals(userId)) {
                    recipientIds.add(user2);
                } else {
                    recipientIds.add(user1);
                }
            }
        }

        return recipientIds.stream()
                .map(userService::getUserById) // Trả về Optional<UserResponse>
                .filter(Optional::isPresent) // Bỏ qua Optional.empty() để tránh lỗi 404
                .map(Optional::get) // Lấy UserResponse từ Optional
                .map(UserChatInfoResponse::new) // Chuyển đổi thành UserChatInfoResponse
                .collect(Collectors.toList());
    }


}
