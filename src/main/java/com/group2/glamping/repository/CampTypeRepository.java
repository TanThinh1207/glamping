package com.group2.glamping.repository;

import com.group2.glamping.model.entity.CampType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//@Repository
//public interface CampTypeRepository extends JpaRepository<CampType, Integer>, JpaSpecificationExecutor<CampType> {
//    @Query(nativeQuery = true,
//            value = "SELECT ct.id AS campTypeId, ct.type, ct.capacity, ct.quantity - COUNT(bd.id) AS remainingQuantity " +
//                    "FROM camp_type ct " +
//                    "LEFT JOIN booking_detail bd ON bd.id_camp_type = ct.id " +
//                    "AND (bd.check_in_at < :checkOutDate AND bd.check_out_at > :checkInDate) " +
//                    "WHERE ct.status = TRUE " +
//                    "AND ct.id = :campTypeId " +
//                    "GROUP BY ct.id")
//    List<Object[]> getRemainingCampTypes(@Param("campTypeId") Integer campTypeId,
//                                         @Param("checkInDate") LocalDateTime checkInDate,
//                                         @Param("checkOutDate") LocalDateTime checkOutDate);

@Repository
public interface CampTypeRepository extends JpaRepository<CampType, Integer>, JpaSpecificationExecutor<CampType> {

    @Query(
            nativeQuery = true,
            value =
                    "SELECT CAST((ct.quantity - COUNT(CASE \n" +
                            "WHEN b.status IS NULL OR b.status = 'Cancelled' \n" +
                            "THEN NULL \n" +
                            "ELSE b.id \n" +
                            "END)) AS SIGNED) AS remaining\n" +
                            "FROM camp_type ct\n" +
                            "LEFT JOIN booking_detail bd ON bd.id_camp_type = ct.id\n" +
                            "LEFT JOIN booking b ON b.id = bd.id_booking\n" +
                            "WHERE ct.id = :campTypeId  \n" +
                            "AND (bd.check_in_at < :checkOutDate  \n" +
                            "AND bd.check_out_at > :checkInDate)  \n" +
                            "GROUP BY ct.id;"

    )
    Long getRemainingCampTypes(
            @Param("campTypeId") Integer campTypeId,
            @Param("checkInDate") LocalDateTime checkInDate,
            @Param("checkOutDate") LocalDateTime checkOutDate
    );

    List<CampType> findByCampSiteId(int campSiteId);

    @Query("SELECT c FROM CampType c WHERE c.type = :type AND c.campSite.id = :campSiteId")
    Optional<CampType> findByTypeAndCampSiteId(@Param("type") String type, @Param("campSiteId") int campSiteId);

}
