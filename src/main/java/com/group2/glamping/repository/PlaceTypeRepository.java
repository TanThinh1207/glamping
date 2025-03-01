package com.group2.glamping.repository;

import com.group2.glamping.model.entity.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceTypeRepository extends JpaRepository<PlaceType, Integer>, JpaSpecificationExecutor<PlaceType> {
}

