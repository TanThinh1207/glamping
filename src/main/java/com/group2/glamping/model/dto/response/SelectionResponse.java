package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("dynamicFilter")
public class SelectionResponse {

    private int id;
    private String name;
    private String description;
    private double price;
    private String image;
    private boolean status;
}
