package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.requests.CampSiteUpdateRequest;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CampSiteService {

    Optional<CampSiteResponse> saveCampSite(CampSiteRequest campSiteUpdateRequest);

    void deleteCampSite(int id);

    PagingResponse<?> getCampSites(Map<String, String> params, int page, int size, String sortBy, String direction);

    Object getFilteredCampSites(Map<String, String> params, int page, int size, String fields, String sortBy, String direction);

    void updateCampSite(int id, CampSiteUpdateRequest campSiteUpdateRequest, List<MultipartFile> files);

}
