package com.group2.glamping.repository;

import com.group2.glamping.model.entity.CampType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CampTypeRepository extends JpaRepository<CampType, Integer> {
}
