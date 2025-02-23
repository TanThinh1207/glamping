package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CampSiteService {

    Optional<CampSiteResponse> saveCampSite(CampSiteRequest campSiteUpdateRequest, List<MultipartFile> files, MultipartFile selectionFile, MultipartFile campTypeFile);

    void deleteCampSite(int id);

    BaseResponse searchCampSiteByNameOrCity(String str);

    Optional<CampSiteResponse> enableCampSite(int id);

    PagingResponse<?> getCampSites(Map<String, String> params, int page, int size);

    MappingJacksonValue getFilteredCampSites(Map<String, String> params, int page, int size, String fields);
}
