package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Integer> {
    Optional<Selection> findByName(String name);

    @Query("SELECT s FROM selection s JOIN s.campSiteList cs WHERE s.name = :name AND cs.id = :campSiteId")
    Optional<Selection> findByNameAndCampSiteId(@Param("name") String name, @Param("campSiteId") int campSiteId);
}


