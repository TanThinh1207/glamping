package com.group2.glamping.repository;

import com.group2.glamping.model.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Integer> {

    @Query("SELECT bd FROM BookingDetail bd WHERE bd.booking.id = :bookingId")
    List<BookingDetail> findBookingDetails(@Param("bookingId") Integer bookingId);
}
