package com.group2.glamping.repository;

import com.group2.glamping.model.dto.response.RatingResponse;
import com.group2.glamping.model.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
                 SELECT new com.group2.glamping.model.dto.response.RatingResponse(
                         b.user.id,
                         CONCAT(b.user.firstname, ' ', b.user.lastname),
                         b.checkOutTime,
                         b.rating,
                         b.comment)
                 FROM booking b
                 WHERE b.campSite.id = :campSiteId\s
                   AND b.rating IS NOT NULL\s
                   AND b.comment IS NOT NULL
            \s""")
    Page<RatingResponse> findAllRatingsByCampSiteId(@Param("campSiteId") Integer campSiteId, Pageable pageable);


    @Query("""
                 SELECT COALESCE(AVG(b.rating), 0)
                 FROM booking b\s
                 WHERE b.campSite.id = :campSiteId
            \s""")
    Double findAverageRatingByCampSiteId(@Param("campSiteId") Integer campSiteId);


}