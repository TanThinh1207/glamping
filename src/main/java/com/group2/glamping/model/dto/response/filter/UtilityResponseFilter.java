package com.group2.glamping.model.dto.response.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtilityResponseFilter {

    private int id;
    private String name;
    private String imagePath;
    private boolean status;
}
