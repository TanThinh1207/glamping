package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.FacilityRequest;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.model.dto.response.PagingResponse;

import java.util.Map;

public interface FacilityService {

    FacilityResponse createFacility(FacilityRequest request);

    FacilityResponse updateFacility(FacilityRequest request);

    PagingResponse<?> getFacilities(Map<String, String> params, int page, int size, String sortBy, String direction);

    Object getFilteredFacilities(Map<String, String> params, int page, int size, String fields, String sortBy, String direction);

    FacilityResponse deleteFacility(Integer id);
}
