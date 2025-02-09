package com.group2.glamping.repository;

import com.group2.glamping.model.entity.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceTypeRepository extends JpaRepository<PlaceType, Integer> {
    Optional<PlaceType> findByName(String name);

    @Query("SELECT p FROM PlaceType p JOIN p.campSites cs WHERE p.name = :name AND cs.id = :campSiteId")
    Optional<PlaceType> findByNameAndCampSiteId(@Param("name") String name, @Param("campSiteId") int campSiteId);
    List<PlaceType> findByNameContainingIgnoreCase(String name);
    List<PlaceType> findByStatus(Boolean status);
}

