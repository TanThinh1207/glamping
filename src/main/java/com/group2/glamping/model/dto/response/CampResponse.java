package com.group2.glamping.model.dto.response;

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
    private LocalDateTime created_at;
    private CampStatus status;
    private LocalDateTime updated_at;

    public static CampResponse fromEntity(Camp camp) {
        if (camp == null) {
            return null;
        }

        return CampResponse.builder()
                .campId(camp.getId())
                .campName(camp.getName())
                .created_at(camp.getCreatedTime())
                .status(camp.getStatus())
                .updated_at(camp.getUpdatedTime())
                .build();
    }
}
