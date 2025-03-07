package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.PlaceTypeRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.PlaceTypeResponse;

import java.util.Map;

public interface PlaceTypeService {

    PlaceTypeResponse createPlaceType(PlaceTypeRequest request);

    PlaceTypeResponse updatePlaceType(PlaceTypeRequest request);

    PagingResponse<?> getPlaceTypes(Map<String, String> params, int page, int size, String sortBy, String direction);

    Object getFilteredPlaceTypes(Map<String, String> params, int page, int size, String fields, String sortBy, String direction);

    PlaceTypeResponse deletePlaceType(Integer id);
}
