package com.group2.glamping.controller;

import com.group2.glamping.model.dto.response.ChatHistoryResponse;
import com.group2.glamping.model.dto.response.ChatMessageResponse;
import com.group2.glamping.model.dto.response.UserChatInfoResponse;
import com.group2.glamping.model.entity.ChatMessage;
import com.group2.glamping.service.interfaces.ChatRedisService;
import com.group2.glamping.service.interfaces.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "Chat API", description = "API for handling chat functionalities")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final ChatRedisService chatRedisService;

    @MessageMapping("/host-chatting")
    @Operation(summary = "Send a public message", description = "Broadcasts a chat message to all users in the public chat room.",
            responses = {@ApiResponse(responseCode = "200", description = "Message sent successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{\"senderId\": 1, \"content\": \"Hello everyone!\"}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = "{\"message\": \"An unexpected error occurred while sending the message\"}")))})
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatService.sendPublicMessage(chatMessage);
    }

    @MessageMapping("/sendToUser")
    @Operation(summary = "Send a private message", description = "Sends a chat message to a specific user.",
            responses = {@ApiResponse(responseCode = "200", description = "Private message sent successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = "{\"message\": \"An unexpected error occurred while sending the private message\"}")))})
    public void sendToUser(@Payload ChatMessage chatMessage) {
        chatService.sendPrivateMessage(chatMessage);
    }

    @GetMapping("/history")
    @Operation(summary = "Get chat history", description = "Retrieves paginated chat history between two users.",
            parameters = {
                    @Parameter(name = "senderId", description = "ID of the sender", example = "1"),
                    @Parameter(name = "recipientId", description = "ID of the recipient", example = "2")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved chat history",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = "[{\"senderId\": 1, \"recipientId\": 2, \"content\": \"Hi\", \"timestamp\": \"2025-03-22T12:00:00Z\"}]"))),
                    @ApiResponse(responseCode = "404", description = "No chat history found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = "{\"message\": \"No chat history found for the given users\"}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = "{\"message\": \"An unexpected error occurred while retrieving chat history\"}")))
            })
    public ResponseEntity<Object> getChatHistory(@RequestParam Integer senderId,
                                                 @RequestParam Integer recipientId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") @Min(value = 2, message = "Page size must be greater than 1") int size,
                                                 @RequestParam(name = "sortBy", required = false, defaultValue = "timestamp") String sortBy,
                                                 @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction) {
        try {
            ChatHistoryResponse chatHistory = chatRedisService.getChatHistory(senderId, recipientId, page, size, sortBy, direction);

            if (chatHistory.getContent().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No chat history found for the given users"));
            }

            return ResponseEntity.ok(chatHistory);
        } catch (Exception e) {
            logger.error("Error retrieving chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred while retrieving chat history"));
        }
    }


    @DeleteMapping("/clear")
    @Operation(
            summary = "Clear chat history",
            description = "Deletes chat history between two users.",
            parameters = {
                    @Parameter(name = "senderId", description = "ID of the sender", example = "1"),
                    @Parameter(name = "recipientId", description = "ID of the recipient", example = "2")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Chat history cleared successfully",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = "{\"message\": \"Chat history cleared successfully\"}"
                            ))
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = "{\"message\": \"Failed to clear chat history\"}"
                            ))
                    )
            }
    )
    public ResponseEntity<Map<String, String>> clearChatHistory(
            @RequestParam Integer senderId,
            @RequestParam Integer recipientId) {
        try {
            chatRedisService.clearChatHistory(senderId, recipientId);
            return ResponseEntity.ok(Map.of("message", "Chat history cleared successfully"));
        } catch (Exception e) {
            logger.error("Failed to clear chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to clear chat history"));
        }
    }


    @GetMapping("/recipients")
    @Operation(summary = "Get chat recipients by user ID", description = "Retrieves a list of users the logged-in user has chatted with.",
            parameters = @Parameter(name = "userId", description = "User ID", example = "1"),
            responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved chat recipients",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "[{\"id\": 2, \"firstname\": \"Manager\", \"email\": \"manager@example.com\"}, {\"id\": 3, \"firstname\": \"Alice\", \"email\": \"alice@example.com\"}]"))),
                    @ApiResponse(responseCode = "404", description = "No recipients found for the given user ID",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = "{\"message\": \"No chat recipients found for userId 1\"}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = "{\"message\": \"An unexpected error occurred while retrieving chat recipients\"}")))})
    public ResponseEntity<?> getRecipientsByUserId(@RequestParam Integer userId) {
        try {
            List<UserChatInfoResponse> recipients = chatRedisService.getRecipientsByUserId(userId);
            if (recipients.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No chat recipients found for userId " + userId);
            }
            return ResponseEntity.ok(recipients);
        } catch (Exception e) {
            logger.error("Error retrieving chat recipients", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while retrieving chat recipients");
        }
    }
}
