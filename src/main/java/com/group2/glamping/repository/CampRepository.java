package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Camp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampRepository extends JpaRepository<Camp, Integer> {
}
