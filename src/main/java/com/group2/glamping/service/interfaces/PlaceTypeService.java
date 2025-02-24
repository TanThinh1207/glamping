package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.PlaceTypeRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.PlaceTypeResponse;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PlaceTypeService {

    PlaceTypeResponse createPlaceType(PlaceTypeRequest request, MultipartFile image);

    PlaceTypeResponse updatePlaceType(PlaceTypeRequest request, MultipartFile image);

    PagingResponse<?> getPlaceTypes(Map<String, String> params, int page, int size);

    MappingJacksonValue getFilteredPlaceTypes(Map<String, String> params, int page, int size, String fields);

    PlaceTypeResponse deletePlaceType(Integer id);
}
