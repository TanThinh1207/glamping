package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Integer> {

    List<Selection> findByNameContainingIgnoreCase(String name);

    List<Selection> findByStatus(boolean status);
}


