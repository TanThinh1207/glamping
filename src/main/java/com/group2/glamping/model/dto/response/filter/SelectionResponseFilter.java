package com.group2.glamping.model.dto.response.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectionResponseFilter {

    private int id;
    private String name;
    private String description;
    private double price;
    private String image;
    private boolean status;
}
