package com.group2.glamping.model.dto.response;

import com.group2.glamping.model.enums.CampSiteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampSiteResponseDTO {
    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private LocalDateTime createdTime;
    private CampSiteStatus status;
    private List<ImageResponse> imageList;
}
