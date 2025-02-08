package com.group2.glamping.model.dto.requests;

import org.springframework.web.multipart.MultipartFile;

public record SelectionRequest(

    Integer id,
    String name,
    String description,
    double price,
    MultipartFile image,
    Integer campSiteId
) {
}
