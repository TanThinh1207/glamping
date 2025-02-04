package com.group2.glamping.repository;

import com.group2.glamping.model.entity.CampType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface CampTypeRepository extends JpaRepository<CampType, Integer> {
    @Query("""
                SELECT (ct.quantity - COUNT(bd.id)) FROM CampType ct
                LEFT JOIN BookingDetail bd ON bd.campType.id = ct.id
                AND ((bd.checkInAt < :checkOutDate AND bd.checkOutAt > :checkInDate))
                WHERE ct.id = :campTypeId
                GROUP BY ct.id
            """)
    Integer countAvailableCamps(
            @Param("campTypeId") Integer campTypeId,
            @Param("checkInDate") LocalDateTime checkInDate,
            @Param("checkOutDate") LocalDateTime checkOutDate
    );
}
