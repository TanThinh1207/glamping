package com.group2.glamping.model.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectionRequest {

    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private boolean status;

}
