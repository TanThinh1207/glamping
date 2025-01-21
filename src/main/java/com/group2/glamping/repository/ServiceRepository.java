package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Order;
import com.group2.glamping.model.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
}
