package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilityRepository extends JpaRepository<Utility, Integer>, JpaSpecificationExecutor<Utility> {

    List<Utility> findByNameContainingIgnoreCase(String name);

    List<Utility> findByStatus(Boolean status);
}

