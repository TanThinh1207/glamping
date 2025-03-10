package com.group2.glamping.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagingResponse<T> {
    private long totalRecords;
    private int totalPages;
    private int currentPage;
    private int currentPageSize;
    private List<T> content;

    public PagingResponse(List<T> content, long totalRecords, int totalPages, int currentPage, int currentPageSize) {
        this.content = content;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.currentPageSize = currentPageSize;
    }
}
