package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Order;
import com.group2.glamping.model.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilityRepository extends JpaRepository<Utility, Integer> {
}
