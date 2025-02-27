package com.group2.glamping.repository;

import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.enums.CampSiteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampSiteRepository extends JpaRepository<CampSite, Integer>, JpaSpecificationExecutor<CampSite> {


}

