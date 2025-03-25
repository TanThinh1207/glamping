package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponseSingle<T> {
    private long totalRecords;
    private int totalPages;
    private int currentPage;
    private int currentPageSize;
    private T content;

    @JsonCreator
    public PagingResponseSingle(
            @JsonProperty("content") T content,
            @JsonProperty("totalRecords") long totalRecords,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("currentPage") int currentPage,
            @JsonProperty("currentPageSize") int currentPageSize) {
        this.content = content;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.currentPageSize = currentPageSize;
    }
}
