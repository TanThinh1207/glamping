package com.group2.glamping.model.dto.requests;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UtilityRequest(
        Integer id,
        String name,
        MultipartFile imagePath
) {
}
