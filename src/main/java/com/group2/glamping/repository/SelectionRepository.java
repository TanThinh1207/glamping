package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Integer>, JpaSpecificationExecutor<Selection> {

}


