package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.entity.CampSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampSiteRepository extends JpaRepository<CampSite, Integer> {
}
