package com.group2.glamping.model.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class ChatHistoryResponse {
    @Builder.Default
    private List<ChatMessageResponse> content = new ArrayList<>();
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public ChatHistoryResponse(List<ChatMessageResponse> content, int totalElements, int totalPages, int currentPage, int pageSize) {
        this.content = (content != null) ? content : new ArrayList<>();
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}
