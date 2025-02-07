package com.group2.glamping.model.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceTypeRequest {

    private int id;
    private String name;
    private String imagePath;
    private boolean status;

}
