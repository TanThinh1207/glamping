package com.group2.glamping.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.requests.CampSiteUpdateRequest;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.dto.response.PagingResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CampSiteService {

    Optional<CampSiteResponse> saveCampSite(CampSiteRequest campSiteUpdateRequest);

    void deleteCampSite(int id);

    PagingResponse<?> getCampSites(Map<String, String> params, int page, int size, String sortBy, String direction) throws JsonProcessingException;

    Object getFilteredCampSites(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) throws JsonProcessingException;

    Object updateCampSite(int id, CampSiteUpdateRequest campSiteUpdateRequest) throws JsonMappingException;

    CampSiteResponse updatePlaceType(int campTypeId, List<Integer> placeTypeIds);

    CampSiteResponse updateUtility(int campTypeId, List<Integer> placeTypeIds);
}
