package com.group2.glamping.model.dto.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public record UtilityRequest(
        Integer id,
        String name,
        MultipartFile image
) {
}
