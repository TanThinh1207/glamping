package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilityRepository extends JpaRepository<Utility, Integer> {

    List<Utility> findByNameContainingIgnoreCase(String name);

    List<Utility> findByStatus(Boolean status);
}

