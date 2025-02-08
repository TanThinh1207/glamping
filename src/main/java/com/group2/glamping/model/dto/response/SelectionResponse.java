package com.group2.glamping.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectionResponse {

    private Integer id;
    private String name;
    private String description;
    private String image;
    private double price;
    private Integer idCampSite;
}
