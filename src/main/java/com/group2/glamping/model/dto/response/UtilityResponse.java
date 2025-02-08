package com.group2.glamping.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtilityResponse {

    private int id;
    private String name;
    private String imagePath;
    private boolean status;
}
