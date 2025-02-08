package com.group2.glamping.repository;

import com.group2.glamping.model.entity.CampType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampTypeRepository extends JpaRepository<CampType, Integer> {
    @Query(nativeQuery = true,
            value = "SELECT ct.quantity - COUNT(bd.id) AS remainingQuantity " +
                    "FROM camp_type ct " +
                    "LEFT JOIN booking_detail bd ON bd.id_camp_type = ct.id " +
                    "AND (bd.check_in_at < :checkOutDate AND bd.check_out_at > :checkInDate) " +
                    "WHERE ct.status = TRUE " +
                    "AND ct.id = :campTypeId " +
                    "GROUP BY ct.id")
    Long getRemainingCampTypes(@Param("campTypeId") Integer campTypeId,
                               @Param("checkInDate") LocalDateTime checkInDate,
                               @Param("checkOutDate") LocalDateTime checkOutDate);

    List<CampType> findByCampSiteId(int campSiteId);
}
