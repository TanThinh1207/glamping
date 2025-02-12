package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CampSiteService {

    List<CampSiteResponse> getAvailableCampSites();

    List<CampSiteResponse> getPendingCampSites();


    Optional<CampSiteResponse> saveCampSite(CampSiteRequest campSiteUpdateRequest, List<MultipartFile> files, MultipartFile selectionFile, MultipartFile campTypeFile);

    Optional<CampSiteResponse> getCampSiteBasicDetail(int id);

    //Optional<CampSiteResponse> updateCampSite(int id, CampSiteRequest campSiteRequest);

    Optional<?> deleteCampSite(int id);

    BaseResponse searchCampSiteByNameOrCity(String str);

    Optional<CampSiteResponse> enableCampSite(int id);
}
