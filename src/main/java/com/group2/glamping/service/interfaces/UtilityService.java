package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.UtilityRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.model.entity.Utility;

import java.util.Map;

public interface UtilityService {
    UtilityResponse createUtility(UtilityRequest request);

    UtilityResponse updateUtility(UtilityRequest request);

    Utility getUtilityById(int id);

    PagingResponse<?> getUtilities(Map<String, String> params, int page, int size, String sortBy, String direction);

    Object getFilteredUtilities(Map<String, String> params, int page, int size, String fields, String sortBy, String direction);

    UtilityResponse softDeleteUtility(int id);

}
