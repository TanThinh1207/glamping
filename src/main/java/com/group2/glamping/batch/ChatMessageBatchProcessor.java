//package com.group2.glamping.batch;
//
//import com.group2.glamping.model.entity.ChatMessage;
////import com.group2.glamping.service.interfaces.ChatRedisService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.Cursor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ScanOptions;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import software.amazon.awssdk.services.s3.endpoints.internal.Value;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class ChatMessageBatchProcessor {
//
////    private final ChatRedisService chatRedisService;
////    private ChatMessageRepository chatMessageRepository;
//    private final RedisTemplate<String, ChatMessage> redisTemplate;
//
//    @Scheduled(fixedRate = 5000) // Chạy mỗi 5 giây
//    public void processChatMessages() {
//        // Dùng scan thay vì keys() để tránh vấn đề performance
//        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection()
//                .scan(ScanOptions.scanOptions().match("chat:*").count(1000).build());
//        List<String> keys = new ArrayList<>();
//        while (cursor.hasNext()) {
//            keys.add(new String(cursor.next()));
//        }
//        try {
//            cursor.close();
//        } catch (Exception e) {
//            // Log lỗi nếu cần
//        }
//
//        // Với mỗi key, lưu tin nhắn vào DB
//        for (String key : keys) {
//            // Lấy conversationId từ key "chat:{conversationId}"
//            Integer conversationId = Integer.parseInt(key.substring(5));
//            List<ChatMessage> messages = chatRedisService.getChatMessages(conversationId);
//            if (messages != null && !messages.isEmpty()) {
////                chatMessageRepository.saveAll(messages);
//                chatRedisService.deleteChatMessages(conversationId);
//            }
//        }
//    }
//}
