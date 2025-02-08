package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilityRepository extends JpaRepository<Utility, Integer> {
    Optional<Utility> findByName(String name);

    @Query("SELECT u FROM utility u JOIN u.campSites cs WHERE u.name = :name AND cs.id = :campSiteId")
    Optional<Utility> findByNameAndCampSiteId(@Param("name") String name, @Param("campSiteId") int campSiteId);
}

