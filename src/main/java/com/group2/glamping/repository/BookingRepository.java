package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM booking b WHERE b.id = :id")
    Optional<Booking> findByIdWithoutDetails(@Param("id") Integer id);


}
