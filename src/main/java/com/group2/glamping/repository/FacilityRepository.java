package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Integer> {
    Optional<Facility> findByName(String name);
}
