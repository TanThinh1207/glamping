package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Integer>, JpaSpecificationExecutor<Selection> {

    List<Selection> findByNameContainingIgnoreCase(String name);

    List<Selection> findByStatus(boolean status);
}


