package com.group2.glamping.repository;

import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.model.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Integer> {
}
