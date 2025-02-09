package com.group2.glamping.model.dto.requests;

import org.springframework.web.multipart.MultipartFile;

public record FacilityRequest(
        Integer id,
        String name,
        String description,
        MultipartFile image
) {
}
