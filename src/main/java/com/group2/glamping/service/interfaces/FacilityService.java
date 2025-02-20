package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.FacilityRequest;
import com.group2.glamping.model.dto.response.FacilityResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FacilityService {

    FacilityResponse createFacility(FacilityRequest request, MultipartFile file);

    FacilityResponse updateFacility(FacilityRequest request, MultipartFile file);

    List<FacilityResponse> getAllFacilities();

    List<FacilityResponse> getFacilityByName(String name);

    List<FacilityResponse> getFacilitiesByStatus(Boolean status);

    FacilityResponse deleteFacility(Integer id);
}
