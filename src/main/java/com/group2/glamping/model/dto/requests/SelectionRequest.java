package com.group2.glamping.model.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
public record SelectionRequest(
        Integer id,
        String name,
        String description,
        double price,
        MultipartFile image,
        Integer campSiteId
) {
}
