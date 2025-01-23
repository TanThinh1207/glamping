package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
        @Query(value = "SELECT ct.capacity - COUNT(bd.id) AS available_slots " +
                "FROM camp_site cs " +
                "JOIN camp_type ct ON cs.id = ct.id_camp_site " +
                "LEFT JOIN booking_detail bd ON bd.id_camp_type = ct.id " +
                "AND (bd.check_in_at < :checkOut AND bd.check_out_at > :checkIn) " +
                "WHERE cs.id = :campSiteId " +
                "GROUP BY cs.id, ct.id " +
                "ORDER BY cs.id", nativeQuery = true)
        Integer findAvailableSlots(
                @Param("campSiteId") Integer campSiteId,
                @Param("checkIn") LocalDateTime checkIn,
                @Param("checkOut") LocalDateTime checkOut
        );

}
