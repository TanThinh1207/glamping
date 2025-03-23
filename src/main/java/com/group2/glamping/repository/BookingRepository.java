package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {

    @Query("SELECT b FROM booking b " +
            "WHERE b.campSite.user.id = :hostId " +
            "AND b.status = 'Completed' " +
            "AND b.checkOutTime BETWEEN :startDate AND :endDate")
    List<Booking> findCompletedBookings(@Param("hostId") Long hostId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
}