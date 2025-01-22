package com.group2.glamping.model.dto.requests;

import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.model.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class CampSiteCreateRequest {

    int userId;
    String name;
    String address;
    double latitude;
    double longitude;
    int quantity;
    List<Image> imageList;
    List<CampType> campTypeList;

}
