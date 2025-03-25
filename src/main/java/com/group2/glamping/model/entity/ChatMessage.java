package com.group2.glamping.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@Entity(name = "chat-messages")
public class ChatMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer senderId;      // User name hoặc id của người gửi

    private Integer recipientId;

    private MessageStatus status;

    @Column(columnDefinition = "TEXT")
    private String content;

    //    @OrderBy("local_datetime ASC")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }
}
