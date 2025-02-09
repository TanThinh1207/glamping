package com.group2.glamping.repository;

import com.group2.glamping.model.entity.BookingSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSelectionRepository extends JpaRepository<BookingSelection, Integer> {

    @Query("SELECT bs FROM booking_selection bs WHERE bs.booking.id = :bookingId")
    List<BookingSelection> findBookingSelections(@Param("bookingId") Integer bookingId);
}
