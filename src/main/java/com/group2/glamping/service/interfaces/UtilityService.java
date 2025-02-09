package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.UtilityRequest;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.model.entity.Utility;

import java.util.List;

public interface UtilityService {
    UtilityResponse createUtility(UtilityRequest request);

    UtilityResponse updateUtility(UtilityRequest request);

    Utility getUtilityById(int id);

    List<UtilityResponse> getAllUtilities();

    List<UtilityResponse> getUtilitiesByName(String name);

    List<UtilityResponse> getUtilitiesByStatus(Boolean status);

    UtilityResponse softDeleteUtility(int id);

}
