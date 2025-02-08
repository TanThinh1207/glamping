package com.group2.glamping.model.dto.requests;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UtilityRequest {

    private int id;
    private String name;
    private String imagePath;
    private boolean status;
}
