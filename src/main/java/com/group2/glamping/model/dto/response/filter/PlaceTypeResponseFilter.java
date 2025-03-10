package com.group2.glamping.model.dto.response.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceTypeResponseFilter {

    private int id;
    private String name;
    private String imagePath;
    private boolean status;

}
