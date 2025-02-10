package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM booking b WHERE b.id = :id")
    Optional<Booking> findByIdWithoutDetails(@Param("id") Integer id);

    @Query("SELECT b FROM booking b WHERE b.campSite.id = :id AND b.status = 'Pending' ")
    List<Booking> findPendingBookingsByCampSiteId(@Param("id") Integer campSiteId);

    @Query("SELECT b FROM booking b " +
            "WHERE b.campSite.id = :id " +
            "AND (b.status = 'Completed' OR b.status = 'Cancelled' OR b.status = 'Refund') ")
    List<Booking> findCompletedBookingsByCampSiteId(@Param("id") Integer campSiteId);
}
