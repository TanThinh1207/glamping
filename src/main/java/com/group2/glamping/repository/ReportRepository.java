package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.Report;
import com.group2.glamping.model.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer>, JpaSpecificationExecutor<Report> {
    long countByCampSiteAndStatus(CampSite campSite, ReportStatus status);
}
