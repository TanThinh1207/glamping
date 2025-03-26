package com.group2.glamping.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampSiteBasicResponse {
    private int id;
    private String name;
    private String address;
}
