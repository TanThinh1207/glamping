package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.FacilityRequest;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FacilityService {

    FacilityResponse createFacility(FacilityRequest request, MultipartFile file);

    FacilityResponse updateFacility(FacilityRequest request, MultipartFile file);

    PagingResponse<?> getFacilities(Map<String, String> params, int page, int size);

    MappingJacksonValue getFilteredFacilities(Map<String, String> params, int page, int size, String fields);

    FacilityResponse deleteFacility(Integer id);
}
