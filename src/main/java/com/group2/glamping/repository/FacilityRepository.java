package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Integer>, JpaSpecificationExecutor<Facility> {

    List<Facility> findByNameContainingIgnoreCase(String name);

    List<Facility> findByStatus(boolean status);

}
