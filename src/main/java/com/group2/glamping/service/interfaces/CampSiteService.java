package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.CampSiteCreateRequest;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.entity.CampSite;

import java.util.List;
import java.util.Optional;

public interface CampSiteService {

    List<CampSiteResponse> getCampSites();

    Optional<CampSite> saveCampSite(CampSiteCreateRequest request);

    Optional<CampSite> findCampSiteById(int id);

    Optional<CampSiteResponse> getCampSiteBasicDetail(int id);

    void updateCampSite(CampSite campSite);

    void deleteCampSite(CampSite campSite);


}
