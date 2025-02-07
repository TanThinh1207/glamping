package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.entity.CampSite;

import java.util.List;
import java.util.Optional;

public interface CampSiteService {

    List<CampSiteResponse> getCampSites();

    Optional<CampSiteResponse> saveCampSite(CampSiteRequest request);

    Optional<CampSite> findCampSiteById(int id);

    Optional<CampSiteResponse> getCampSiteBasicDetail(int id);

    Optional<CampSiteResponse> updateCampSite(int id, CampSiteRequest campSiteRequest);

    Optional<?> deleteCampSite(int id);

    BaseResponse searchCampSiteByNameOrCity(String str);

}
