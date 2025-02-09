package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.PlaceTypeRequest;
import com.group2.glamping.model.dto.response.PlaceTypeResponse;

import java.util.List;

public interface PlaceTypeService {

    PlaceTypeResponse createPlaceType(PlaceTypeRequest request);

    PlaceTypeResponse updatePlaceType(PlaceTypeRequest request);

    List<PlaceTypeResponse> getAllPlaceTypes();

    List<PlaceTypeResponse> getPlaceTypeByName(String name);

    List<PlaceTypeResponse> getPlaceTypeByStatus(Boolean status);

    PlaceTypeResponse deletePlaceType(Integer id);
}
