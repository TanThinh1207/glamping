package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.entity.Camp;
import com.group2.glamping.model.enums.CampStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampResponse {

    private int campId;
    private String campName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private CampStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static CampResponse fromEntity(Camp camp) {
        if (camp == null) {
            return null;
        }

        return CampResponse.builder()
                .campId(camp.getId())
                .campName(camp.getName())
                .createdAt(camp.getCreatedTime())
                .status(camp.getStatus())
                .updatedAt(camp.getUpdatedTime())
                .build();
    }
}
