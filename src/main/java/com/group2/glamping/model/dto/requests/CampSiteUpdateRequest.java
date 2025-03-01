package com.group2.glamping.model.dto.requests;

import com.group2.glamping.model.enums.CampSiteStatus;
import lombok.Builder;

@Builder
public record CampSiteUpdateRequest(
        String name,
        String address,
        CampSiteStatus status
) {
}




